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

  private val nearCookieKey = "near"

  private val searchForm = Form(
    mapping("near" -> nonEmptyText)(PlacesCriteria.apply)(PlacesCriteria.unapply)
  )

  private val searchExecutor = (c: PlacesCriteria) => placesService.findPlacesNear(c.near)

  private val handleFailure = (t: Throwable) =>
    InternalServerError(views.html.places_search_failed(t.getMessage))

  private val handleSuccess = (c: PlacesCriteria, p: Option[Seq[Place]]) =>
    Ok(views.html.places_results(p)).withCookies(Cookie(nearCookieKey, c.near))

  private def findPlacesUsingTimeout(getPlaces: PlacesCriteria => Future[Option[Seq[Place]]], searchCriteria: PlacesCriteria)
                                    (handleSuccess: (PlacesCriteria, Option[Seq[Place]]) => Result)
                                    (handleFailure: Throwable => Result): Future[Result] = {
    firstCompletedOf {
      Seq(getPlaces(searchCriteria), timeout("Fetching places timed out", 5.second))
    }.map {
      case places: Option[Seq[Place]] => handleSuccess(searchCriteria, places)
      case t: String => handleFailure(new PlacesRetrievalException(t))
    }.recover {
      case t: Throwable => handleFailure(t)
    }
  }

  def index = Action { request =>
    val previousSearch = request.cookies.get(nearCookieKey).map(_.value).getOrElse("London")
    Ok(views.html.places_index(searchForm.fillAndValidate(PlacesCriteria(previousSearch))))
  }

  def search = Action.async { implicit request =>
    searchForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest(views.html.places_index(formWithErrors))),
      criteria => findPlacesUsingTimeout(searchExecutor, criteria)(handleSuccess)(handleFailure)
    )
  }

}
