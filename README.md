# Description

This application is a "popular places search" web application that integrates against the Foursquare API.
It is built using the Play Framework, utilising the MVC pattern and Bootstrap for presentation.

# Foursquare APIs
The integration is against the Foursquare explore venues API:

https://developer.foursquare.com/docs/venues/explore

# Technologies Used

- Scala 2.11
- Play Framework 2.4
- HTML / Javascript
- Bootstrap 3
- Testing using Specs2, Mockito and Selenium

# Patterns and Techniques Used

- MVC
- Adaptor
- Dependency Injection
- Functional Programming
- OOD
- TDD

# Setup

- Install activator using Homebrew:
    **brew install typesafe-activator**
- Ensure Java 8 SDK is installed and set as JAVA_HOME:
    **export JAVA_HOME=/location**
- Run the tests (from the project root):
    **activator test**
- Run a specific test:
    **activator "test-only BrowserFunctionalSpec"**
- Run the application in auto-compile mode:
    **activator run**
- Create a distribution zip for the application:
    **sbt universal:packageBin**

# Executable Distribution

There is a distribution zip file in the project root which contains a runnable application

- Unzip the zip
- Ensure Java 8 is set
- Run from the root folder:
    **./bin/foursquare-search-play**
- Open browser to:
    **[http://localhost:9000](http://localhost:9000)**

# Testing

The tests consist of unit/integration tests and end-to-end functional tests.

- Controllers - These have tests which mock out the service using Mockito
- Views - Have isolated unit tests to check that the views load/parse correctly
- Service - The Foursquare adaptor test uses a mock web service to mock the Foursquare API
- End to End - There are tests from the routes, and separate browser-based tests (Selenium based)
Browser based tests can tend to more fragile, hence the route tests serve as API level tests which are much more stable

# Approach Taken

The places search logic is hidden behind an interface (PlacesService) and a Place abstraction model to decouple the application
from Foursquare. An Foursquare adaptor implementation is then used specialise this interface, hence providing the
encapsulation and decoupling.

The approach was to focus on getting a working front-end as quickly (and incrementally) as possible to allow the presentation
to be demonstrated by mocking out the Places data using a stub. Bootstrap was used to speed up the presentation / UX side of things.
Play's MVC framework made this easy to do by separating the Model from the View and Controller side.

Once this was working, the focus was on getting the 'happy path' working in the Foursquare adaptor and hooked into the
application dependency injection framework to further improve the demo. This provided real data to the UI, and something
more meaningful to the user.

Then, further TDD was used to drive out the other scenarios in the Foursquare adaptor e.g. failure, no results. Once this was complete,
refactoring took place to create smaller, higher order functions to separate out the side effects and resulted in purer/cleaner functions, which can be more easily tested as needed.

Next, some further scenarios were added to the PlacesController to support timeouts, and display to the user a suitable
message. The controller was refactored to create smaller functions, and higher order functions where possible, again to
align with functional programming best practices.

Finally, cookies were incorporated to save the last search, so that the user can come back and just hit 'Submit'.

Scala was used mostly because of recent familiarity with it, and also because it is a strongly typed functional language. This resulted in cleaner code that is easy to refactor and maintain, and results in less runtime errors. As a side note, the Play framework is a 'reactive' framework that incorporates Futures, Actors and a non-blocking web server. Therefore, it is suitable platform for growing the application further into something that is production-ready and highly scalable.

# Screenshots
![ScreenShot Index](https://raw.githubusercontent.com/spiritedtechie/foursquare-app-play/master/search_index.png)
![ScreenShot Results](https://raw.githubusercontent.com/spiritedtechie/foursquare-app-play/master/search_results.png)



