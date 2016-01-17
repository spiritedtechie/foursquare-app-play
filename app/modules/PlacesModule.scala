package modules

import com.google.inject.AbstractModule
import services.{PlacesServiceStub, PlacesService}

class PlacesModule extends AbstractModule {

  def configure() = {
    bind(classOf[PlacesService]).to(classOf[PlacesServiceStub]).asEagerSingleton()
  }
}
