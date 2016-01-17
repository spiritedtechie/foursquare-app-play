package models

case class Location(address: Option[String] = None,
                    city: Option[String] = None,
                    country: Option[String] = None,
                    postalCode: Option[String] = None)

case class Contact(phone: Option[String] = None)

case class Place(name: String,
                 url: Option[String] = None,
                 location: Option[Location] = None,
                 contact: Option[Contact] = None,
                 rating: Option[BigDecimal] = None)