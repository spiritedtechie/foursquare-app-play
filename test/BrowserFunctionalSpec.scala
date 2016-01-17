import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._

/**
 * Test end to end from browser including any external services, via Selenium.
 */
@RunWith(classOf[JUnitRunner])
class BrowserFunctionalSpec extends Specification {

  "Application" should {

    "land on places search page" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.title() must equalTo("Search For Places")
    }

    "should be able to search for place" in new WithBrowser {

      browser.goTo("http://localhost:" + port)
      browser.fill("#near").`with`("Coventry")
      browser.submit("#search-submit")

      browser.title() must equalTo("Places Search Results")
      browser.findFirst("body h2").getText must equalTo("Results Found (30)")
      browser.find("#places-results tbody tr").size() must equalTo(60)
    }

    "should be able to navigate back to search index" in new WithBrowser {

      browser.goTo("http://localhost:" + port)
      browser.fill("#near").`with`("Coventry")
      browser.submit("#search-submit")
      browser.title() must equalTo("Places Search Results")

      browser.click("#search-home")

      browser.title() must equalTo("Search For Places")
    }
  }
}
