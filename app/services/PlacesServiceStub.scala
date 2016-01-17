package services

import models.{Contact, Location, Place}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlacesServiceStub extends PlacesService {
  override def findPlacesNear(name: String) = Future {
    Some(
      Seq(
        Place("Hampstead Heath", Some("http://www.cityoflondon.gov.uk/hampstead"), Some(Location(Some("E Heath Rd"), Some("London"), Some("United Kingdom"), Some("NW3 2PT"))), Some(Contact(Some("02073323322"))), Some(9.7)),
        Place("Hyde Park", Some("http://www.royalparks.org.uk/parks/hyde-park"), Some(Location(Some("Serpentine Rd"), Some("London"), Some("United Kingdom"), Some("W2 2TP"))), Some(Contact(None)), Some(9.6))
      )
    )
  }
}
