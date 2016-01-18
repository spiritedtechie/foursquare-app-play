package controllers

import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Redirect(routes.PlacesController.index())
  }

}
