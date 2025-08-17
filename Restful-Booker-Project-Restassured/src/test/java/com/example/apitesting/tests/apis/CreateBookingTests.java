package com.example.apitesting.tests.apis;

import com.example.apitesting.api.endpoints.BookingEndpoints;
import com.example.apitesting.base.BaseTest;
import com.example.apitesting.models.Booking;
import com.example.apitesting.models.BookingDates;
import com.example.apitesting.utils.TestDataGenerator;
import com.example.apitesting.api.RestClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@Epic("API Regression Testing")
@Feature("Booking API")
public class CreateBookingTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(CreateBookingTests.class);

    @Test(description = "Verify successful booking creation")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Create a new booking with valid data")
    public void testCreateBooking() {
        String firstname = TestDataGenerator.generateRandomFirstName();
        String lastname = TestDataGenerator.generateRandomLastName();
        int totalprice = TestDataGenerator.generateRandomTotalPrice();
        boolean depositpaid = TestDataGenerator.generateRandomDepositPaid();
        String checkin = TestDataGenerator.generateRandomCheckinDate();
        String checkout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking bookingPayload = new Booking(firstname, lastname, totalprice, depositpaid, bookingDates, "Breakfast");

        Response response = BookingEndpoints.createBooking(bookingPayload);

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createBookingSchema.json"));

        assertNotNull(response.jsonPath().getString("bookingid"), "Booking ID should not be null");
        assertEquals(response.jsonPath().getString("booking.firstname"), firstname, "First name mismatch");
        assertEquals(response.jsonPath().getString("booking.lastname"), lastname, "Last name mismatch");
        assertEquals(response.jsonPath().getInt("booking.totalprice"), totalprice, "Total price mismatch");
        assertEquals(response.jsonPath().getBoolean("booking.depositpaid"), depositpaid, "Deposit paid mismatch");
        assertEquals(response.jsonPath().getString("booking.bookingdates.checkin"), checkin, "Checkin date mismatch");
        assertEquals(response.jsonPath().getString("booking.bookingdates.checkout"), checkout, "Checkout date mismatch");
        assertEquals(response.jsonPath().getString("booking.additionalneeds"), "Breakfast", "Additional needs mismatch");

        logger.info("Created Booking ID: " + response.jsonPath().getString("bookingid"));
        logger.info("Response Body: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }


}


