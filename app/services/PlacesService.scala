package services

import models.Place

import scala.concurrent.Future

trait PlacesService {

  def findPlacesNear(name: String): Future[Option[Seq[Place]]];

}
