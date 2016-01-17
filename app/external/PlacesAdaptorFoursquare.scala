package external

import javax.inject.Inject

import models.Place
import play.api.libs.ws.WSClient
import services.PlacesService

import scala.concurrent.Future

class PlacesAdaptorFoursquare @Inject()(ws: WSClient) extends PlacesService {

  def findPlacesNear(name: String): Future[Option[Seq[Place]]] = {
    ???
  }
}
