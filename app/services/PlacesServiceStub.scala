package services

import models.Place

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlacesServiceStub extends PlacesService {
  override def findPlacesNear(name: String) = Future {
    Some(Seq(new Place("Awesome place")))
  }
}
