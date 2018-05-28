package RestfulBooker;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static io.restassured.RestAssured.given;

public class RestfulBooker {
    //API endpoints
    public String baseUrl = "https://restful-booker.herokuapp.com/";
    public String pingUrl = this.baseUrl.concat("ping");
    public String bookingUrl = this.baseUrl.concat("booking");
    public String authUrl = this.baseUrl.concat("auth");

    public RestfulBooker() {
    }

    /**
     * Return example contract file for booking post request
     * @return String example booking post request
     */
    public String getExampleOfBookingPostRequest() {
        return getFile("./booking-post-req-example.json");
    }

    /**
     * Return example contract file for auth request
     * @return String example auth request
     */
    public String getExampleOfUserAuthorization() {
        return getFile("./booking-post-auth-req.json");
    }

    /**
     * Return api endpoint for get particular booking for endpoint /booking/{id}
     * @param bookingId id used for creating endpoint
     * @return String endpoint URL
     */
    public String getBookingUrlForBookingId(int bookingId) {
        return this.bookingUrl.concat("/").concat(String.valueOf(bookingId));
    }

    /**
     * create request for calling get/booking with precised parameter i.e.:
     * GET /booking?firstname=sally&lastname=brown
     * GET /booking?checkin=2014-03-13&checkout=2014-05-21
     *
     * @param firstParameterName  parameters name
     * @param secondParameterName
     * @param firstParameterValue parameters value
     * @param secondParameterValue
     * @return String api endpoint with passed parameters
     */
    public String getBookingUrlQueryingByTwoGetParameters(String firstParameterName, String firstParameterValue, String secondParameterName, String secondParameterValue) {
        String parameters = firstParameterName.concat("=").concat(firstParameterValue)
                .concat("&".concat(secondParameterName).concat("=").concat(secondParameterValue));
        return this.getBookingUrlForQueryingBooking(parameters);
    }

    /**
     * Get auth token from API
     *
     * @return String auth token value
     */
    public String getAuthToken() {
        return this.extractAuthToken(this.getAuthRequestResponse());
    }

    /**
     * Send booking request to api
     *
     * @return int booking id get after performing post/booking
     */
    public int sendNewBookingRequest() {
        return this.getBookingIdFromPostResponse(getBookingRequestResponse());
    }

    /**
     * Modifies particular attribute and return example booking post request from resources.
     *
     * @param attributeToReplace name of attribute to replace
     * @param newValue           new value of attribute
     * @return booking post request body
     */
    public String getModifiedExampleBookingPostRequest(String attributeToReplace, String newValue) {
        JSONObject modifiedJson = new JSONObject(this.getExampleOfBookingPostRequest()).put(attributeToReplace, newValue);
        return modifiedJson.toString();
    }

    /**
     * Modifies particular attribute and return example booking post request from resources.
     *
     * @param attributeToReplace name of attribute to replace
     * @param newValue           new value of attribute
     * @return booking post request body
     */
    public String getModifiedExampleBookingPostRequest(String attributeToReplace, Number newValue) {
        JSONObject modifiedJson = new JSONObject(this.getExampleOfBookingPostRequest()).put(attributeToReplace, newValue);
        return modifiedJson.toString();
    }

    /**
     * Modifies particular attribute and return example booking post request from resources.
     *
     * @param attributeToReplace name of attribute to replace
     * @param newValue           new value of attribute
     * @return booking post request body
     */
    public String getModifiedExampleBookingPostRequest(String attributeToReplace, boolean newValue) {
        JSONObject modifiedJson = new JSONObject(this.getExampleOfBookingPostRequest()).put(attributeToReplace, newValue);
        return modifiedJson.toString();
    }

    /**
     * Return attribute value form example booking request from resources
     *
     * @param attribute name of attribute to return value
     * @return String attribute value
     */
    public String getAttributeValueFromJsonExamplePostRequest(String attribute) {
        return new JSONObject(this.getExampleOfBookingPostRequest()).get(attribute).toString();

    }

    /**
     * Return nested attribute value form example booking request from resources
     *
     * @param parentAttribute name of parent attribute
     * @param attribute       name of attribute to return value
     * @return String attribute value
     */
    public String getNestedAttributeValueFromJsonExamplePostRequest(String parentAttribute, String attribute) {
        return new JSONObject(this.getExampleOfBookingPostRequest()).getJSONObject(parentAttribute).get(attribute).toString();
    }

    /**
     * Return token value form authorization response
     *
     * @param authPostResponse body of authorization response
     * @return String token value
     */
    private String extractAuthToken(String authPostResponse) {
        return new JSONObject(authPostResponse).getString("token");
    }

    /**
     * Concat booking endpoint and parameters for calling booking id with precised attributes
     *
     * @param getParameters get request parameters
     * @return String url for perform get request
     */
    private String getBookingUrlForQueryingBooking(String getParameters) {
        return this.bookingUrl.concat("?").concat(getParameters);
    }

    /**
     * Extract bookingId from body of post response
     *
     * @param bookingPostResponse body returned after post/booking
     * @return int posted booking ID
     */
    private int getBookingIdFromPostResponse(String bookingPostResponse) {
        return new JSONObject(bookingPostResponse).getInt("bookingid");
    }

    /**
     * Return response from performed post request
     *
     * @param postUrl endpoint for post request
     * @param body    of request
     * @return String response after performing post request
     */
    private String performRequest(String postUrl, String body) {
        return given()
                .request().headers("Content-Type", "application/json")
                .body(body)
                .post(postUrl).body().print();
    }

    /**
     * Return response from post/auth
     *
     * @return String response after performing post/auth
     */
    private String getAuthRequestResponse() {
        return this.performRequest(this.authUrl, this.getExampleOfUserAuthorization());
    }

    /**
     * Return response from post/booking
     *
     * @return String response after performing post/booking
     */
    private String getBookingRequestResponse() {
        return this.performRequest(this.bookingUrl, this.getExampleOfBookingPostRequest());
    }

    /**
     * Return file from resources
     *
     * @param fileName from resources
     * @return String representation of read file
     */
    private String getFile(String fileName) {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();

    }
}
