package com.example.apitesting.tests;

import com.example.apitesting.api.endpoints.BookingEndpoints;
import com.example.apitesting.base.BaseTest;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertNotNull;

@Epic("End-to-End API Testing")
@Feature("Booking Lifecycle")
public class E2ETests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(E2ETests.class);
    private int bookingId;
    private String token;

    @Test(description = "End-to-End Test: Create, Get, Update, Partial Update, Delete Booking")
    @Severity(SeverityLevel.CRITICAL)
    public void testBookingLifecycle() {
        authenticateAndGetToken();

        createBooking();

        getBooking();

        updateBooking();

        partialUpdateBooking();

        deleteBooking();
    }

    @Step("Step 1: Authenticate and get token")
    private void authenticateAndGetToken() {
        logger.info("Attempting to get authentication token.");
        token = TokenManager.getToken();
        assertNotNull(token, "Authentication token should not be null");
        logger.info("Authentication token obtained: " + token);
    }

    @Step("Step 2: Create Booking")
    private void createBooking() {
        String firstName = TestDataGenerator.generateRandomFirstName();
        String lastName = TestDataGenerator.generateRandomLastName();
        int totalPrice = TestDataGenerator.generateRandomTotalPrice();
        boolean depositPaid = TestDataGenerator.generateRandomDepositPaid();
        String checkin = TestDataGenerator.generateRandomCheckinDate();
        String checkout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking bookingPayload = new Booking(firstName, lastName, totalPrice, depositPaid, bookingDates, "Breakfast");

        logger.info("Creating booking with payload: " + bookingPayload);
        Response response = BookingEndpoints.createBooking(bookingPayload);

        response.then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("booking.firstname", equalTo(firstName))
                .body("booking.lastname", equalTo(lastName));

        bookingId = response.jsonPath().getInt("bookingid");
        logger.info("Booking created successfully with ID: " + bookingId);
    }

    @Step("Step 3: Get Booking")
    private void getBooking() {
        logger.info("Getting booking with ID: " + bookingId);
        Response response = BookingEndpoints.getBooking(bookingId);

        response.then()
                .statusCode(200)
                .body("firstname", notNullValue());
        logger.info("Booking retrieved successfully.");
    }

    @Step("Step 4: Update Booking")
    private void updateBooking() {
        String updatedFirstName = TestDataGenerator.generateRandomFirstName();
        String updatedLastName = TestDataGenerator.generateRandomLastName();
        int updatedTotalPrice = TestDataGenerator.generateRandomTotalPrice();
        boolean updatedDepositPaid = TestDataGenerator.generateRandomDepositPaid();
        String updatedCheckin = TestDataGenerator.generateRandomCheckinDate();
        String updatedCheckout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates updatedBookingDates = new BookingDates(updatedCheckin, updatedCheckout);
        Booking updatedBookingPayload = new Booking(updatedFirstName, updatedLastName, updatedTotalPrice, updatedDepositPaid, updatedBookingDates, "Dinner");

        logger.info("Updating booking with ID: " + bookingId + " with payload: " + updatedBookingPayload);
        Response response = BookingEndpoints.updateBooking(bookingId, updatedBookingPayload, token);

        response.then()
                .statusCode(200)
                .body("firstname", equalTo(updatedFirstName))
                .body("lastname", equalTo(updatedLastName));
        logger.info("Booking updated successfully.");
    }

    @Step("Step 5: Partial Update Booking")
    private void partialUpdateBooking() {
        String partialUpdatedFirstName = TestDataGenerator.generateRandomFirstName();
        String partialUpdatedLastName = TestDataGenerator.generateRandomLastName();

        java.util.Map<String, Object> partialPayload = new java.util.HashMap<>();
        partialPayload.put("firstname", partialUpdatedFirstName);
        partialPayload.put("lastname", partialUpdatedLastName);

        logger.info("Partially updating booking with ID: " + bookingId + " with payload: " + partialPayload);
        Response response = BookingEndpoints.partialUpdateBooking(bookingId, partialPayload, token);

        response.then()
                .statusCode(200)
                .body("firstname", equalTo(partialUpdatedFirstName))
                .body("lastname", equalTo(partialUpdatedLastName));
        logger.info("Booking partially updated successfully.");
    }

    @Step("Step 6: Delete Booking")
    private void deleteBooking() {
        logger.info("Deleting booking with ID: " + bookingId);
        Response response = BookingEndpoints.deleteBooking(bookingId, token);

        response.then()
                .statusCode(201);

        Response getResponse = BookingEndpoints.getBooking(bookingId);
        getResponse.then().statusCode(404);
        logger.info("Booking deleted successfully.");
    }
}

