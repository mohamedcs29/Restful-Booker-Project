package com.example.apitesting.tests;

import com.example.apitesting.api.RestClient;
import com.example.apitesting.api.endpoints.AuthEndpoints;
import com.example.apitesting.api.endpoints.BookingEndpoints;
import com.example.apitesting.base.BaseTest;
import com.example.apitesting.models.Auth;
import com.example.apitesting.models.Booking;
import com.example.apitesting.models.BookingDates;
import com.example.apitesting.utils.TestDataGenerator;
import com.example.apitesting.utils.TokenManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertFalse;

@Epic("API Error Handling")
@Feature("Negative Scenarios")
public class ErrorScenarioTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(ErrorScenarioTests.class);

    @Test(description = "Verify authentication with invalid credentials")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to authenticate with incorrect username and password")
    public void testAuthWithInvalidCredentials() {
        logger.info("Attempting authentication with invalid credentials.");
        Auth authPayload = new Auth("invalid_user", "invalid_password");
        Response response = AuthEndpoints.createToken(authPayload);

        response.then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
        logger.info("Authentication with invalid credentials tested successfully.");
    }


    @Test(description = "Verify getting a non-existent booking")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to retrieve a booking with a non-existent ID")
    public void testGetNonExistentBooking() {
        logger.info("Attempting to get a non-existent booking.");
        int nonExistentBookingId = 999999;
        Response response = BookingEndpoints.getBooking(nonExistentBookingId);

        response.then()
                .statusCode(404);
        logger.info("Getting non-existent booking tested successfully.");
    }

    @Test(description = "Verify updating a non-existent booking")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to update a booking with a non-existent ID and valid token")
    public void testUpdateNonExistentBooking() {
        logger.info("Attempting to update a non-existent booking.");
        int nonExistentBookingId = 999999;
        String token = TokenManager.getToken();

        String firstName = TestDataGenerator.generateRandomFirstName();
        String lastName = TestDataGenerator.generateRandomLastName();
        int totalPrice = TestDataGenerator.generateRandomTotalPrice();
        boolean depositPaid = TestDataGenerator.generateRandomDepositPaid();
        String checkin = TestDataGenerator.generateRandomCheckinDate();
        String checkout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking bookingPayload = new Booking(firstName, lastName, totalPrice, depositPaid, bookingDates, "Breakfast");

        Response response = BookingEndpoints.updateBooking(nonExistentBookingId, bookingPayload, token);

        response.then()
                .statusCode(405);
        logger.info("Updating non-existent booking tested successfully.");
    }

    @Test(description = "Verify deleting a non-existent booking")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to delete a booking with a non-existent ID and valid token")
    public void testDeleteNonExistentBooking() {
        logger.info("Attempting to delete a non-existent booking.");
        int nonExistentBookingId = 999999;
        String token = TokenManager.getToken();

        Response response = BookingEndpoints.deleteBooking(nonExistentBookingId, token);

        response.then()
                .statusCode(405);
        logger.info("Deleting non-existent booking tested successfully.");
    }

    @Test(description = "Verify updating a booking with invalid token")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to update a booking with an invalid token")
    public void testUpdateBookingWithInvalidToken() {
        logger.info("Attempting to update a booking with invalid token.");
        int bookingId = 1;
        String invalidToken = "invalid_token";

        String firstName = TestDataGenerator.generateRandomFirstName();
        String lastName = TestDataGenerator.generateRandomLastName();
        int totalPrice = TestDataGenerator.generateRandomTotalPrice();
        boolean depositPaid = TestDataGenerator.generateRandomDepositPaid();
        String checkin = TestDataGenerator.generateRandomCheckinDate();
        String checkout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking bookingPayload = new Booking(firstName, lastName, totalPrice, depositPaid, bookingDates, "Breakfast");

        Response response = BookingEndpoints.updateBooking(bookingId, bookingPayload, invalidToken);

        response.then()
                .statusCode(403);
        logger.info("Updating booking with invalid token tested successfully.");
    }

    @Test(description = "Verify deleting a booking with invalid token")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to delete a booking with an invalid token")
    public void testDeleteBookingWithInvalidToken() {
        logger.info("Attempting to delete a booking with invalid token.");
        int bookingId = 1;
        String invalidToken = "invalid_token";

        Response response = BookingEndpoints.deleteBooking(bookingId, invalidToken);

        response.then()
                .statusCode(403);
        logger.info("Deleting booking with invalid token tested successfully.");
    }

    @Test(description = "Verify booking creation with invalid JSON")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to create a booking with invalid JSON payload")
    public void testCreateBookingInvalidJson() {
        String invalidJson = "{\"firstname\": \"Test\", \"lastname\": \"User\", \"totalprice\": \"invalid_price\"}";

        Response response = RestClient.post("/booking", invalidJson);

        response.then()
                .statusCode(anyOf(is(400), is(500)));

        assertFalse(response.asString().contains("bookingid"), "Booking was created unexpectedly!");

        logger.info("Response Body for invalid JSON: " + response.asString());
    }
}

