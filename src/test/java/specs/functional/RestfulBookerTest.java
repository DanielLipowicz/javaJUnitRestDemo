package specs.functional;

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

public class RestfulBookerTest {
    private RestfulBooker restBooker;
    String req = "";

    @Before
    public void createRestfulBookerTestObject() {
        restBooker = new RestfulBooker();
    }
//    As User I want to create+, read+-, update+ and delete- booking on https://restful-booker.herokuapp.com/

    @Test
    public void checkingAppAvailabilityBooking() {
        // Booking app should return 200 status after ping
        when().get(restBooker.pingUrl).
                then().statusCode(200);
    }

    @Test
    public void getAllBooking() {
        // As a user I should be able to read all bookings id
        when().get(restBooker.bookingUrl)
                .then().assertThat()
                .body(matchesJsonSchemaInClasspath("booking-get-all-schema.json"))
                .body("bookingid",hasItems(1));
    }
    @Test
    public void getOneBooking() {
        // As a user I should be able to read all bookings id
        when().get(restBooker.getBookingUrlForBookingId(1))
                .then().assertThat()
                .body(matchesJsonSchemaInClasspath("./booking-get-one-res-schema"))
                .body("size()",is(6));
    }

    @Test
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

//    @Test
//    public void createBooking(){
//        //As a user I able to create booking with particular ID
//        int bookingID = 1;
//        restBooker.createBooking(bookingID);
//
//        //then I should be able to see my booking ID
//
//          IsArrayContaining(restBooker.readIDs(),bookingID);
//
//    }
}
