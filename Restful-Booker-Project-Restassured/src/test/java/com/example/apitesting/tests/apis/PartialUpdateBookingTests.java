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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("API Regression Testing")
@Feature("Booking API")
public class PartialUpdateBookingTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(PartialUpdateBookingTests.class);
    private int bookingId;
    private String token;

    @BeforeClass
    public void setup() {
        token = TokenManager.getToken();
        String firstName = TestDataGenerator.generateRandomFirstName();
        String lastName = TestDataGenerator.generateRandomLastName();
        int totalPrice = TestDataGenerator.generateRandomTotalPrice();
        boolean depositPaid = TestDataGenerator.generateRandomDepositPaid();
        String checkin = TestDataGenerator.generateRandomCheckinDate();
        String checkout = TestDataGenerator.generateRandomCheckoutDate();
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking bookingPayload = new Booking(firstName, lastName, totalPrice, depositPaid, bookingDates, "Breakfast");

        Response createResponse = BookingEndpoints.createBooking(bookingPayload);
        createResponse.then().statusCode(200);
        bookingId = createResponse.jsonPath().getInt("bookingid");
        logger.info("Setup: Created booking with ID: " + bookingId + " for partial update tests.");
    }

    @Test(description = "Verify partial update of booking with valid data")
    @Severity(SeverityLevel.NORMAL)
    @Step("Partially update booking with new first name and last name")
    public void testPartialUpdateBooking() {
        String newFirstName = TestDataGenerator.generateRandomFirstName();
        String newLastName = TestDataGenerator.generateRandomLastName();

        Map<String, Object> partialPayload = new HashMap<>();
        partialPayload.put("firstname", newFirstName);
        partialPayload.put("lastname", newLastName);

        logger.info("Partially updating booking ID: " + bookingId + " with payload: " + partialPayload);
        Response response = BookingEndpoints.partialUpdateBooking(bookingId, partialPayload, token);

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/partialUpdateBookingSchema.json"))
                .body("firstname", equalTo(newFirstName))
                .body("lastname", equalTo(newLastName));

        logger.info("Partial update successful for booking ID: " + bookingId);
    }




}

