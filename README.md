# Description

This web application allows a user to find popular places near to a queried place. It shows the following data about each result:

- Name
- Address
- Telephone
- Rating

It is built using the Play Framework, utilising the MVC pattern and Bootstrap for presentation. Play is a Reactive framework, utilising non-blocking IO, Futures and Actors - thus supporting high scalability out of the box.

The purpose of this project was to solve a simple problem that allowed me to explore some of the basics of the Play Framework. It does not do much in terms of computation, just simply wrapping a Foursquare API with a web application. 

# Foursquare Integration

The search is against the Foursquare explore venues API, which provides the places results.

https://developer.foursquare.com/docs/venues/explore

# Technologies Used

- HTML / Javascript
- Bootstrap 3
- Scala 2.11.6
- Sbt 0.13.8
- Google Guice 4
- Play Framework 2.4.6
- Play JSON libraries
- Play WS client
- Testing using Specs2, Mockito and Selenium

# Patterns Used

- MVC
- Adaptor
- Dependency Injection

# Techniques Used

- SOLID
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

The tests consist of integration tests, and end-to-end functional tests.

- Controllers - Have integration tests. PlacesService is mocked using Mockito.
- Views - Have integration tests to check that the views load/parse correctly. The view data is mocked.
- Service - Have integration tests to the remote API. A mock web service is used to mock the remote Foursquare API.
- End to End - There are separate functional tests from the routes and from the browser (Selenium based). Browser-based tests tend to be more fragile, hence the route tests serve as API level tests which are much more stable.

# Approach Taken

The places search logic is hidden behind an interface (PlacesService) and a Place abstraction model to decouple the application from Foursquare. An Foursquare adaptor implementation is then used specialise this interface, hence providing the encapsulation and decoupling.

The approach was to focus on getting a working front-end as quickly (and incrementally) as possible to allow the presentation to be demonstrated by mocking out the Places data using a stub service implementation. Bootstrap was used to speed up the presentation / UX side of things. Play's MVC framework made this easy to do by separating the Model from the View and Controller side, allowing it to be mocked easily.

Once this was working, the focus was on getting the 'happy path' working in the Foursquare adaptor (against the Foursquare API) and hooked into the application dependency injection framework to further improve the demo. This provided real data to the UI, and something more meaningful to the user.

Then, further TDD was used to drive out the other scenarios in the Foursquare adaptor e.g. failure, no results. Once this was complete, refactoring took place to create smaller, higher order functions to separate out the side effects and resulted in purer/cleaner functions, which can be more easily tested as needed.

Next, some further scenarios were added to the PlacesController to support timeouts, and display to the user a suitable error message. The controller was refactored to create smaller functions, and higher order functions where possible, again to align with functional programming best practices.

Finally, cookies were incorporated to save the last search, so that the user can come back and just hit 'Submit'.

Scala & Play were used mostly because of recent familiarity and interest in it. Also it is a strongly-typed functional language which resulted in cleaner code that is easy to refactor and maintain. As a side note, the Play framework is a 'reactive' framework that incorporates Futures, Actors and a non-blocking web server. Therefore, it is suitable platform for growing the application further into something that is production-ready and highly scalable.

# Screenshots
![ScreenShot Index](https://raw.githubusercontent.com/spiritedtechie/foursquare-app-play/master/search_index.png)
![ScreenShot Results](https://raw.githubusercontent.com/spiritedtechie/foursquare-app-play/master/search_results.png)



