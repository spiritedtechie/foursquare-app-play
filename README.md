# Description

This application is a "popular places search" web application that integrates against the Foursquare API.
It is built using the Play Framework, utilising the MVC pattern and Bootstrap 3 for presentation.

# Technologies Overview

- Scala
- PlayFramework libraries
- HTML
- Javascript
- Bootstrap
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
- Again, ensure Java 8 is set
- Run from the root folder:
    **./bin/foursquare-search-play**
- Open browser to:
    **http://localhost:9000**

# Testing

The tests consist of unit/integration tests and end to end functional tests.

- Controllers - These have tests which mocks out the service using Mockito
- Views - Have isolated unit tests to check that they load/parse correctly
- Service - The Foursquare adaptor service uses a mock web service to mock the Foursquare API
- End to End - There are tests for the application from the routes, and separate browser based tests (Selenium based).
Browser based tests can tend to more fragile, hence the route tests serve as API level tests which are much more stable.

# Approach Taken

The places search logic is hidden behind an interface (PlacesService) and a Place abstraction model to decouple the application
from Foursquare. An Foursquare adaptor implementation is then used specialise this interface, hence providing the
encapsulation and decoupling.

The approach was to focus on getting a working front-end as quickly (and incrementally) as possible to allow the presentation
to be demonstrated by mocking out the Places data using a stub. Bootstrap was used to speed up the presentation / UX side of things.
Play's MVC framework made this easy to do by separating the Model from the View and Controller side.

Once this was working, the focus was on getting the happy path working in the Foursquare adaptor and hooked into the
application dependency injection framework to further improve the demo. This provided real data to the UI, and something
more meaningful to the user.

Then, further TDD was used to drive out the other scenarios in the Foursquare adaptor e.g. failure, no results. Once this was complete,
refactoring took place to create smaller functions and use higher order functions to separate out the side effects and
result in purer/cleaner functions, which can be more easily tested if needed.

Next, some further scenarios were added to the PlacesController to support timeouts, and display to the user a suitable
message. The controller was refactored to create smaller functions, and higher order functions where possible.

Finally cookies were incorporated to save the last search.

Scala was used mostly because of recent familiarity, and also it is a strongly typed language and functional so
errors were less likely to occur dynamically, and code could be refactored using functional pattern resulting in cleaner code.
As a side note, the Play framework is a 'reactive' framework that incorporates Futures, Actors and a non-blocking web server.
Therefore it is suitable platform for growing the application further into something that is production and scale worthy.







