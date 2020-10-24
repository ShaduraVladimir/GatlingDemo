package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RecordedSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://demo.nopcommerce.com")
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0")
    .disableFollowRedirect

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_1 = Map(
    "Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
    "Origin" -> "https://demo.nopcommerce.com",
    "X-Requested-With" -> "XMLHttpRequest")


  val scn = scenario("RecordedSimulation")
    // Open home page
    .exec(http("Open home page")
      .get("/")
      .check(currentLocation)
      .headers(headers_0))
    .pause(1)
    // Search products by keyword
    .exec(http("Search by keyword")
      .get("/search?q=samsung")
      .check(status.is("200"))
      .check(substring("samsung"))
      .headers(headers_0))
    .pause(1)
    // Open product page
    .exec(http("Open product page")
      .get("/samsung-series-9-np900x4c-premium-ultrabook")
      .check(status.is("200"))
      .check(substring("product-details-page"))
      .headers(headers_0))
    .pause(1)
    // Add to cart
    .exec(http("Add to cart")
      .post("/addproducttocart/details/6/1")
      .check(status.is("200"))
      .headers(headers_1))
    .pause(1)
    // Go to shopping cart
    .exec(http("Go to shopping cart")
      .get("/cart")
      .check(currentLocation)
      .check(regex("<span class=cart-qty>(.+?)</span>").is("(1)"))
      .headers(headers_0))

  setUp(scn.inject(atOnceUsers(1))).assertions(global.successfulRequests.percent.gt(95)).protocols(httpProtocol)
}