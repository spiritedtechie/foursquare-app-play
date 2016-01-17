package exceptions

case class PlacesRetrievalException(val message: String) extends RuntimeException(message);
