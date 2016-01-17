package controllers

import play.api.mvc.Results
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

class ApplicationSpec extends PlaySpecification with Results {

  "Application controller" should {

    "redirect" in new WithApplication {
      val result = new Application().index(FakeRequest())

      status(result) must equalTo(SEE_OTHER)
    }

    "have no content" in new WithApplication() {
      val result = new Application().index(FakeRequest())

      contentAsString(result) must beEmpty
    }

    "redirect to places" in new WithApplication() {
      val result = new Application().index(FakeRequest())

      redirectLocation(result) must beSome("/places")
    }
  }

}
