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
import scala.util.{Failure, Success, Try}

class PlacesAdaptorFoursquare @Inject()(ws: WSClient, configuration: Configuration) extends PlacesService {

  private val unknown = "Unknown"
  private lazy val foursquareBaseUrl = configuration.getString("app.foursquare.baseUrl").getOrElse(unknown)
  private lazy val foursquareExploreVenuesUrl = s"${foursquareBaseUrl}/v2/venues/explore"
  private lazy val clientId = configuration.getString("app.foursquare.clientId").getOrElse(unknown)
  private lazy val clientSecret = configuration.getString("app.foursquare.clientSecret").getOrElse(unknown)

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

  private def requestForPopularVenuesNear(name: String)
                                         (url: String)
                                         (clientId: String, clientSecret: String) =
    ws.url(url)
      .withQueryString("near" -> name)
      .withQueryString("client_id" -> s"${clientId}")
      .withQueryString("client_secret" -> s"${clientSecret}")
      .withQueryString("v" -> "20150113")

  private val tryMapToPlaces = (json: JsValue) => Try {
    ((json \ "response" \ "groups")(0) \ "items").validate[Seq[Place]] match {
      case s: JsSuccess[Seq[Place]] => Some(s.value)
      case e: JsError => throw new PlacesRetrievalException("Failure when parsing data from Foursquare")
    }
  }

  private def handlingJsonResponseCode(json: JsValue)
                                      (tryMapJsonToPlaces: JsValue => Try[Some[Seq[Place]]]) = {
    (json \ "meta" \ "code").toOption match {
      case Some(JsNumber(code)) if code == 200 => tryMapJsonToPlaces(json)
      case Some(JsNumber(code)) if code == 400 => Try(None)
      case _ => throw new PlacesRetrievalException("Failure when finding places from Foursquare")
    }
  }

  override def findPlacesNear(name: String): Future[Option[Seq[Place]]] = {
    requestForPopularVenuesNear(name)(foursquareExploreVenuesUrl)(clientId, clientSecret).get().map {
      response => handlingJsonResponseCode(response.json)(tryMapToPlaces) match {
        case Success(p) => p
        case Failure(e) => throw e
      }
    }
  }
}
