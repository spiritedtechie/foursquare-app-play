package external

import javax.inject.Inject

import exceptions.PlacesRetrievalException
import models.{Contact, Location, Place}
import play.api.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import services.PlacesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlacesAdaptorFoursquare @Inject()(ws: WSClient, configuration: Configuration) extends PlacesService {

  private lazy val foursquareBaseUrl = configuration.getString("app.foursquare.baseUrl").getOrElse("Unknown")

  private implicit val locationReads: Reads[Location] =
    (
      (JsPath \ "address").readNullable[String] and
      (JsPath \ "city").readNullable[String] and
      (JsPath \ "country").readNullable[String] and
      (JsPath \ "postalCode").readNullable[String]
      )(Location.apply _)

  private implicit val personFormatter: Format[Contact] =
    (JsPath \ "phone").formatNullable[String].inmap(Contact.apply, unlift(Contact.unapply))

  private implicit val placeReads: Reads[Place] =
    (
      (JsPath \ "venue" \ "name").read[String] and
      (JsPath \ "venue" \ "url").readNullable[String] and
      (JsPath \ "venue" \ "location").readNullable[Location] and
      (JsPath \ "venue" \ "contact").readNullable[Contact] and
      (JsPath \ "venue" \ "rating").readNullable[BigDecimal]
      )(Place.apply _)

  private def handlingJsonResponseCode(json: JsValue) = {
    (json \ "meta" \ "code").toOption match {
      case Some(JsNumber(code)) if code == 200 => {
        val d = ((json \ "response" \ "groups")(0) \ "items").validate[Seq[Place]]

        d match {
          case s: JsSuccess[Seq[Place]] => Some(s.value)
          case e: JsError => throw new PlacesRetrievalException("Failure when parsing data from Foursquare")
        }
      }
      case Some(JsNumber(code)) if code == 400 => None
      case _ => throw new PlacesRetrievalException("Failure when finding places from Foursquare")
    }
  }

  override def findPlacesNear(name: String): Future[Option[Seq[Place]]] = {

    val request =
      ws.url(s"${foursquareBaseUrl}/v2/venues/explore")
        .withQueryString("near" -> name)
        .withQueryString("client_id" -> "G13G1JGM3EA4FXORGPFBMPGBDSBMYMR1S1KYV2OAUF33PQV4")
        .withQueryString("client_secret" -> "ADGLVHHMGASP4RRKDY0O2UKQZKUD1XLLJV505M0JJ3A4LSB2")
        .withQueryString("v" -> "20150113")

    request.get().map { response => handlingJsonResponseCode(response.json) }
  }
}
