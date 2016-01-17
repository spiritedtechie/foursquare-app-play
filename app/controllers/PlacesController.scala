package controllers

import javax.inject.Inject

import exceptions.PlacesRetrievalException
import models.{Place, PlacesCriteria}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.libs.concurrent.Promise._
import play.api.mvc._
import services.PlacesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future._
import scala.concurrent.duration._


class PlacesController @Inject()(placesService: PlacesService) extends Controller {

  private val searchForm = Form(
    mapping("near" -> nonEmptyText)(PlacesCriteria.apply)(PlacesCriteria.unapply)
  )

  private val handleFailure = (t: Throwable) =>
    InternalServerError(views.html.places_search_failed(t.getMessage))

  private val handleSuccess = (c: PlacesCriteria, p: Option[Seq[Place]]) =>
    Ok(views.html.places_results(p)).withCookies(Cookie("near", c.near))

  private def findPlacesWithTimeout(searchCriteria: PlacesCriteria)
                                   (handleSuccess: (PlacesCriteria, Option[Seq[Place]]) => Result)
                                   (handleFailure: Throwable => Result): Future[Result] = {

    val placesFuture = placesService.findPlacesNear(searchCriteria.near)
    val timeoutFuture = timeout("Fetching places timed out", 5.second)

    firstCompletedOf(Seq(placesFuture, timeoutFuture)).map {
      case places: Option[Seq[Place]] => handleSuccess(searchCriteria, places)
      case t: String => handleFailure(new PlacesRetrievalException(t))
    }.recover {
      case t: Throwable => handleFailure(t)
    }
  }

  def index = Action { request =>
    Ok(views.html.places_index(searchForm.fillAndValidate(PlacesCriteria("London"))))
  }

  val search = Action.async { implicit request =>
    searchForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest(views.html.places_index(formWithErrors))),
      searchCriteria => findPlacesWithTimeout(searchCriteria)(handleSuccess)(handleFailure)
    )
  }

}
