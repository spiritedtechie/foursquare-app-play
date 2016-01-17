import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

/**
 * Test end to end from routes including any external services
 */
@RunWith(classOf[JUnitRunner])
class RoutesFunctionalSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
    }

    "redirect to places index when /" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(SEE_OTHER)
      redirectLocation(home) must beSome("/places")
    }

    "route to places index when /places" in new WithApplication() {
      val result = route(FakeRequest(GET, "/places")).get

      status(result) must equalTo(OK)
      contentAsString(result) must contain("Search For Places")
    }

    "route to places search when POST /places/search" in new WithApplication {
      val result = route(FakeRequest(POST, "/places/search").withFormUrlEncodedBody("near" -> "London")).get

      status(result) must equalTo(OK)
      contentAsString(result) must contain("Places Search Results")
    }
  }
}
