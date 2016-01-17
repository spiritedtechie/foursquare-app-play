package views

import models.PlacesCriteria
import org.specs2.mock.Mockito
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.test.{PlaySpecification, WithApplication}

class ViewsSpec extends PlaySpecification with Mockito {

  private val searchForm = Form(
    mapping("near" -> nonEmptyText)(PlacesCriteria.apply)(PlacesCriteria.unapply)
  )

  implicit val messagesMock = mock[Messages]

  "render index template" in new WithApplication() {
    val html = views.html.places_index(searchForm)

    contentAsString(html) must contain("Search For Places")
  }

}
