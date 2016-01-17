package controllers

import _root_.exceptions.PlacesRetrievalException
import models.Place
import org.specs2.mock.Mockito
import play.api.mvc.{Cookie, Results}
import play.api.test._
import services.PlacesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PlacesControllerSpec {

  val testPlace = new Place("Awesome place")
}

class PlacesControllerSpec extends PlaySpecification with Results with Mockito {

  import PlacesControllerSpec._

  def mockService = {
    val m = mock[PlacesService]
    m.findPlacesNear(any[String]) returns Future(Some(Seq(testPlace)))
    m
  }

  def mockServiceWhichFails = {
    val m = mock[PlacesService]
    m.findPlacesNear(any[String]) returns Future.failed(new PlacesRetrievalException("Failure when finding places from backend"))
    m
  }

  def mockServiceWhichTimesOut = {
    val m = mock[PlacesService]
    m.findPlacesNear(any[String]) returns Future {
      Thread.sleep(6000)
      None
    }
    m
  }

  "Places index" should {

    "return OK" in new WithApplication {
      val result = new PlacesController(mockService).index(FakeRequest())

      status(result) must equalTo(OK)
    }

    "send to places index view" in new WithApplication() {
      val result = new PlacesController(mockService).index(FakeRequest())

      contentAsString(result) must contain("<title>Search For Places</title>")
    }

    "populate places index view with default if no cookie" in new WithApplication() {
      val result = new PlacesController(mockService).index(FakeRequest())

      contentAsString(result) must contain("<input type=\"text\" id=\"near\" name=\"near\" value=\"London\" />")
    }

    "populate places index view with previous search if cookie" in new WithApplication() {
      val result = new PlacesController(mockService).index(FakeRequest().withCookies(Cookie("near", "Manchester")))

      contentAsString(result) must contain("<input type=\"text\" id=\"near\" name=\"near\" value=\"Manchester\" />")
    }

    "populate places index view with previous search if cookie (encoded value)" in new WithApplication() {
      val result = new PlacesController(mockService).index(FakeRequest().withCookies(Cookie("near", "Camden+Town")))

      contentAsString(result) must contain("<input type=\"text\" id=\"near\" name=\"near\" value=\"Camden Town\" />")
    }
  }

  "Places search happy path" should {

    "call places service if valid" in new WithApplication {
      val mock = mockService
      val result = new PlacesController(mock).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      there was one(mock).findPlacesNear("Daventry")
    }

    "return OK" in new WithApplication {
      val result = new PlacesController(mockService).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      status(result) must equalTo(OK)
    }

    "show places results" in new WithApplication {

      val result = new PlacesController(mockService).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      contentAsString(result) must contain("Places Search Results")
    }

    "set cookie with entered search" in new WithApplication {

      val result = new PlacesController(mockService).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      cookies(result).get("near") must beEqualTo(Some(Cookie("near", "Daventry")))
    }

    "set cookie with entered search encoded if needed" in new WithApplication {

      val result = new PlacesController(mockService).search(FakeRequest().withFormUrlEncodedBody("near" -> "Camden Town"))

      cookies(result).get("near") must beEqualTo(Some(Cookie("near", "Camden+Town")))
    }
  }

  "Places search where search times out" should {

    "return InternalServerError" in new WithApplication {

      val result = new PlacesController(mockServiceWhichTimesOut).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      status(result) must equalTo(INTERNAL_SERVER_ERROR)
    }

    "send to search failed view" in new WithApplication() {
      val result = new PlacesController(mockServiceWhichTimesOut).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      contentAsString(result) must contain("Search Failed")
      contentAsString(result) must contain("Fetching places timed out")
    }
  }

  "Places search fails" should {

    "return InternalServerError" in new WithApplication {

      val result = new PlacesController(mockServiceWhichFails).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      status(result) must equalTo(INTERNAL_SERVER_ERROR)
    }

    "send to search failed view" in new WithApplication() {
      val result = new PlacesController(mockServiceWhichFails).search(FakeRequest().withFormUrlEncodedBody("near" -> "Daventry"))

      contentAsString(result) must contain("Search Failed")
      contentAsString(result) must contain("Failure when finding places from backend")
    }
  }
}
