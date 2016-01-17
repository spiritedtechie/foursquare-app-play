package views

import models.{Place, PlacesCriteria}
import org.specs2.mock.Mockito
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.test.{PlaySpecification, WithApplication}

object ViewsSpec {

  val testPlace = new Place("Awesome place")
  val testPlace2 = new Place("Another Awesome place")
}

class ViewsSpec extends PlaySpecification with Mockito {

  import ViewsSpec._

  private val searchForm = Form(
    mapping("near" -> nonEmptyText)(PlacesCriteria.apply)(PlacesCriteria.unapply)
  )

  implicit val messagesMock = mock[Messages]

  "render index template" in new WithApplication() {
    val html = views.html.places_index(searchForm)

    contentAsString(html) must contain("Search For Places")
  }

  "render search results template" in new WithApplication() {
    val html = views.html.places_results(Some(Seq(testPlace, testPlace2)))

    contentAsString(html) must contain("Places Search Results")
    contentAsString(html) must contain("Results Found (2)")
  }

  "render search failed template" in new WithApplication() {
    val html = views.html.places_search_failed("some failure reason")

    contentAsString(html) must contain("Search Failed")
    contentAsString(html) must contain("some failure reason")
  }

}
