package controllers

import org.specs2.mock.Mockito
import play.api.mvc.{Cookie, Results}
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlacesControllerSpec extends PlaySpecification with Results with Mockito {

  "Places index" should {

    "return OK" in new WithApplication {
      val result = new PlacesController().index(FakeRequest())

      status(result) must equalTo(OK)
    }

    "send to places index view" in new WithApplication() {
      val result = new PlacesController().index(FakeRequest())

      contentAsString(result) must contain("<title>Search For Places</title>")
    }
  }

}
