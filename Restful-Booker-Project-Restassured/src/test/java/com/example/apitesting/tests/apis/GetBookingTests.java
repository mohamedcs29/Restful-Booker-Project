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

import static org.testng.Assert.assertNotNull;

@Epic("API Regression Testing")
@Feature("Booking API")
public class GetBookingTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(GetBookingTests.class);
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

    @Test(description = "Verify retrieving a booking by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Retrieve booking details using a valid booking ID")
    public void testGetBookingById() {
        Response response = BookingEndpoints.getBooking(bookingId);

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/getBookingSchema.json"));

        assertNotNull(response.jsonPath().getString("firstname"), "First name should not be null");
        assertNotNull(response.jsonPath().getString("lastname"), "Last name should not be null");
        assertNotNull(response.jsonPath().getString("totalprice"), "Total price should not be null");
        assertNotNull(response.jsonPath().getString("depositpaid"), "Deposit paid should not be null");
        assertNotNull(response.jsonPath().getString("bookingdates.checkin"), "Checkin date should not be null");
        assertNotNull(response.jsonPath().getString("bookingdates.checkout"), "Checkout date should not be null");

        logger.info("Retrieved Booking Details: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }

    @Test(description = "Verify retrieving all booking IDs")
    @Severity(SeverityLevel.NORMAL)
    @Step("Retrieve all booking IDs")
    public void testGetAllBookingIds() {
        Response response = BookingEndpoints.getAllBookings();

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/searchBookingSchema.json"));

        assertNotNull(response.jsonPath().getList("bookingid"), "Booking IDs list should not be null");
        logger.info("All Booking IDs: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }

}
