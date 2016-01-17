package external

import models.{Contact, Location, Place}
import org.specs2.specification.core.Env
import play.api.Configuration
import play.api.mvc._
import play.api.test._
import play.core.server.Server

import scala.concurrent.duration._

class PlacesAdaptorFoursquareSpec(env: Env) extends PlaySpecification {

  implicit val ee = env.executionEnv
  implicit val ec = env.executionContext

  // Creates an adaptor which uses a mock of the foursquare web service to allow
  // integration testing against a real service
  def withPlacesAdaptorForFoursquare[T](jsonFile: String)(block: PlacesAdaptorFoursquare => T): T = {
    Server.withRouter() {
      case rh if rh.method == GET => Action(Results.Ok.sendResource(jsonFile))
    } { implicit port =>
      WsTestClient.withClient { client =>
        block(new PlacesAdaptorFoursquare(client, Configuration("app.foursquare.baseUrl" -> "")))
      }
    }
  }

  "findPlacesNear happy path" should {
    "should get venues from Foursquare" in {

      val expectedPlaces = Seq(
        Place("Hampstead Heath", Some("http://www.cityoflondon.gov.uk/hampstead"), Some(Location(Some("E Heath Rd"), Some("London"), Some("United Kingdom"), Some("NW3 2PT"))), Some(Contact(Some("02073323322"))), Some(9.7)),
        Place("Hyde Park", Some("http://www.royalparks.org.uk/parks/hyde-park"), Some(Location(Some("Serpentine Rd"), Some("London"), Some("United Kingdom"), Some("W2 2TP"))), Some(Contact(None)), Some(9.6))
      )

      withPlacesAdaptorForFoursquare("foursquare_venues_explore_success.json") {
        adaptor =>
          val result = adaptor.findPlacesNear("London")
          result must beEqualTo(Some(expectedPlaces)).awaitFor(3 seconds)
      }
    }
  }
}
