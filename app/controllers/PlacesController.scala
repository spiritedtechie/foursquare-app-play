package controllers

import models.PlacesCriteria
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

class PlacesController extends Controller {

  private val searchForm = Form(
    mapping("near" -> nonEmptyText)(PlacesCriteria.apply)(PlacesCriteria.unapply)
  )

  def index = Action { request =>
    Ok(views.html.places_index(searchForm.fillAndValidate(PlacesCriteria("London"))))
  }

}
