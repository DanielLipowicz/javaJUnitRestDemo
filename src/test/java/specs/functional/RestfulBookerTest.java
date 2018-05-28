package specs.functional;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.http.Cookie;
import org.junit.Before;
import org.junit.Test;
import RestfulBooker.RestfulBooker;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

@Epic("Restful Booker - Junit 4 - API Tests")
public class RestfulBookerTest {
    private RestfulBooker restBooker;
    String req = "";

    @Before
    public void createRestfulBookerTestObject() {
        restBooker = new RestfulBooker();
    }

    @Test
    @Feature("Call restful - GET /ping")
    public void checkingAppAvailabilityBooking() {
        // Booking app should return 200 status after ping
        when().get(restBooker.pingUrl).
                then().statusCode(200);
    }

    @Test
    @Feature("Call restful - GET /booking")
    public void getAllBooking() {
        // As a user I should be able to read all bookings id
        when().get(restBooker.bookingUrl)
                .then().assertThat()
                .body(matchesJsonSchemaInClasspath("booking-get-all-schema.json"))
                .body("bookingid",hasItems(1));
    }

    @Test
    @Feature("Call restful - GET /booking?checkin=2014-03-13&checkout=2014-05-21")
    public void getOneBookingByBookingDates() {
        // As a user I should be able to read all bookings id
        restBooker.sendNewBookingRequest();
        String checkin = restBooker.getNestedAttributeValueFromJsonExamplePostRequest("bookingdates","checkin");
        String checkout = restBooker.getNestedAttributeValueFromJsonExamplePostRequest("bookingdates","checkout");

        when().get(restBooker.getBookingUrlQueryingByTwoGetParameters("checkin",checkin, "checkout",checkout))
                .then().assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("booking-get-all-schema.json"));
    }

    @Test
    @Feature("Call restful - GET /booking?firstname=sally&lastname=brown")
    public void getOneBookingByNameAndSurname() {
        // As a user I should be able to get booking by name and surname
        //create booking for test purpose and create token from api
        restBooker.sendNewBookingRequest();
        String name = restBooker.getAttributeValueFromJsonExamplePostRequest("firstname");
        String surname = restBooker.getAttributeValueFromJsonExamplePostRequest("lastname");

        when().get(restBooker.getBookingUrlQueryingByTwoGetParameters("name",name,"surname",surname))
                .then().assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("booking-get-all-schema.json"));
    }
    @Test
    @Feature("Call restful - GET /booking/{id}")
    public void getOneBookingById() {
        // As a user I should be able to get booking by check in and check out date
        //create booking for test purpose
        int bookingId = restBooker.sendNewBookingRequest();
        when().get(restBooker.getBookingUrlForBookingId(bookingId))
                .then().assertThat()
                .body(matchesJsonSchemaInClasspath("./booking-get-one-res-schema"))
                .body("size()",is(6));
    }

    @Test
    @Feature("Call restful - POST /booking")
    public void postBooking() {
        // As a user i should be able to post my booking
        given()
                .request()
                .headers("Content-Type", "application/json")
                .body(restBooker.getExampleOfBookingPostRequest())
                .when()
                .post(restBooker.bookingUrl)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("./booking-post-res-schema.json"));

    }

    @Test
    @Feature("Call restful - PUT /booking/{id}")
    public void editBooking() {
        // As a user i should be able to edit my posted booking
        //create booking for test purpose and create token from api
        int bookingId = restBooker.sendNewBookingRequest();
        Cookie tokenCookie =
                new Cookie.Builder("token", restBooker.getAuthToken())
                        .setDomain("restful-booker.herokuapp.com")
                        .setPath("/").setHttpOnly(false).setSecured(false).build();

        given()
                .headers("Content-Type", "application/json")
                .cookie(tokenCookie)
                .body(restBooker.getModifiedExampleBookingPostRequest("firstname", "Danny"))
                .when()
                .put(restBooker.getBookingUrlForBookingId(bookingId))
                .then().assertThat().statusCode(200)
                .body("firstname", equalTo("Danny"));
    }

    @Test
    @Feature("Call restful - DELETE /booking/{id}")
    public void deleteBooking() {
        // As a user i should be able to edit my posted booking
        //create booking for test purpose and create token from api
        int bookingId = restBooker.sendNewBookingRequest();
        Cookie tokenCookie =
                new Cookie.Builder("token", restBooker.getAuthToken())
                        .setDomain("restful-booker.herokuapp.com")
                        .setPath("/").setHttpOnly(false).setSecured(false).build();

        given()
                .headers("Content-Type", "application/json")
                .cookie(tokenCookie)
                .when()
                .delete(restBooker.getBookingUrlForBookingId(bookingId))
                .then().assertThat().statusCode(204);
        //and particular booking should not be able
        when().get(restBooker.getBookingUrlForBookingId(bookingId))
                .then().assertThat().statusCode(404);
    }
}
