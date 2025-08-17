package com.example.apitesting.tests.apis;

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
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Epic("API Regression Testing")
@Feature("Booking API")
public class UpdateBookingTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(UpdateBookingTests.class);
    private int bookingId;
    private String token;

    @BeforeClass
    public void setupBooking() {
        String firstname = TestDataGenerator.generateRandomFirstName();
        String lastname = TestDataGenerator.generateRandomLastName();
        int totalprice = TestDataGenerator.generateRandomTotalPrice();
        boolean depositpaid = TestDataGenerator.generateRandomDepositPaid();
        String checkin = TestDataGenerator.generateRandomCheckinDate();
        String checkout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking bookingPayload = new Booking(firstname, lastname, totalprice, depositpaid, bookingDates, "Dinner");

        Response createResponse = BookingEndpoints.createBooking(bookingPayload);
        createResponse.then().statusCode(200);
        bookingId = createResponse.jsonPath().getInt("bookingid");
        logger.info("Setup: Created Booking ID: " + bookingId);


        token = TokenManager.getToken();
        logger.info("Setup: Retrieved Token from TokenManager: " + token);
    }

    @Test(description = "Verify successful booking update")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Update an existing booking with valid data")
    public void testUpdateBooking() {
        String updatedFirstname = TestDataGenerator.generateRandomFirstName();
        String updatedLastname = TestDataGenerator.generateRandomLastName();
        int updatedTotalprice = TestDataGenerator.generateRandomTotalPrice();
        boolean updatedDepositpaid = !TestDataGenerator.generateRandomDepositPaid(); // Toggle deposit paid
        String updatedCheckin = TestDataGenerator.generateRandomCheckinDate();
        String updatedCheckout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates updatedBookingDates = new BookingDates(updatedCheckin, updatedCheckout);
        Booking updatedBookingPayload = new Booking(updatedFirstname, updatedLastname, updatedTotalprice, updatedDepositpaid, updatedBookingDates, "Lunch");

        Response response = BookingEndpoints.updateBooking(bookingId, updatedBookingPayload, token);

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/updateBookingSchema.json"));

        assertEquals(response.jsonPath().getString("firstname"), updatedFirstname, "First name mismatch after update");
        assertEquals(response.jsonPath().getString("lastname"), updatedLastname, "Last name mismatch after update");
        assertEquals(response.jsonPath().getInt("totalprice"), updatedTotalprice, "Total price mismatch after update");
        assertEquals(response.jsonPath().getBoolean("depositpaid"), updatedDepositpaid, "Deposit paid mismatch after update");
        assertEquals(response.jsonPath().getString("bookingdates.checkin"), updatedCheckin, "Checkin date mismatch after update");
        assertEquals(response.jsonPath().getString("bookingdates.checkout"), updatedCheckout, "Checkout date mismatch after update");
        assertEquals(response.jsonPath().getString("additionalneeds"), "Lunch", "Additional needs mismatch after update");

        logger.info("Updated Booking Details: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }

    @Test(description = "Verify booking update with invalid token")
    @Severity(SeverityLevel.NORMAL)
    @Step("Attempt to update a booking with an invalid token")
    public void testUpdateBookingInvalidToken() {
        String updatedFirstname = TestDataGenerator.generateRandomFirstName();
        String updatedLastname = TestDataGenerator.generateRandomLastName();
        int updatedTotalprice = TestDataGenerator.generateRandomTotalPrice();
        boolean updatedDepositpaid = !TestDataGenerator.generateRandomDepositPaid();
        String updatedCheckin = TestDataGenerator.generateRandomCheckinDate();
        String updatedCheckout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates updatedBookingDates = new BookingDates(updatedCheckin, updatedCheckout);
        Booking updatedBookingPayload = new Booking(updatedFirstname, updatedLastname, updatedTotalprice, updatedDepositpaid, updatedBookingDates, "Lunch");

        Response response = BookingEndpoints.updateBooking(bookingId, updatedBookingPayload, "invalid_token");

        response.then()
                .statusCode(403);

        assertEquals(response.asString(), "Forbidden", "Response body should be 'Forbidden'");
        logger.info("Response Body for invalid token: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }
}

