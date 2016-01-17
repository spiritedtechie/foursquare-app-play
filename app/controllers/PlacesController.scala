package controllers

import javax.inject.Inject

import models.PlacesCriteria
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import services.PlacesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlacesController @Inject()(placesService: PlacesService) extends Controller {

  private val searchForm = Form(
    mapping("near" -> nonEmptyText)(PlacesCriteria.apply)(PlacesCriteria.unapply)
  )

  def index = Action { request =>
    Ok(views.html.places_index(searchForm.fillAndValidate(PlacesCriteria("London"))))
  }

  val search = Action.async { implicit request =>
    searchForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest(views.html.places_index(formWithErrors))),
      searchCriteria => {
        val placesFuture = placesService.findPlacesNear(searchCriteria.near)
        placesFuture.map { p => Ok(views.html.places_results(p)) }.recover {
          case t: Throwable => InternalServerError(views.html.places_search_failed(t.getMessage))
        }
      }
    )
  }

}
