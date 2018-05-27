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

    public String getExampleOfBookingPostRequest() {
        return getFile("./booking-post-req-example.json");
    }

    public String getAndModifyExampleOfBookingPostRequest(String attribute, String newValue){
        JSONObject modifiedJson = new JSONObject(this.getExampleOfBookingPostRequest()).put(attribute,newValue);
        return modifiedJson.toString();
    }
    public String getAndModifyExampleOfBookingPostRequest(String attribute, Number newValue){
        JSONObject modifiedJson = new JSONObject(this.getExampleOfBookingPostRequest()).put(attribute,newValue);
        return modifiedJson.toString();
    }

    public String getBookingUrlForBookingId(int bookingId){
        return this.bookingUrl.concat("/").concat(String.valueOf(bookingId));
    }
    public String getExampleOfUserAuthorization() {
        return getFile("./booking-post-auth-req.json");
    }

    public int getBookingIdFromPostResponse(String bookingPostResponse) {
        return new JSONObject(bookingPostResponse).getInt("bookingid");
    }

    public String getAuthTokenFromAuthRequest(String authPostResponse) {
        return new JSONObject(authPostResponse).getString("token");
    }

    public String getAuthToken(){
        this.getAuthTokenFromAuthRequest(given()
                .request().headers("Content-Type", "application/json")
                .body(this.getExampleOfUserAuthorization())
                .post(this.authUrl).body().print()
        );
    }

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
