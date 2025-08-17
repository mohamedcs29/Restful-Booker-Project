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
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Epic("API Regression Testing")
@Feature("Booking API")
public class DeleteBookingTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(DeleteBookingTests.class);
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

    @Test(description = "Verify successful booking deletion")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Delete an existing booking with valid token")
    public void testDeleteBooking() {
        Response response = BookingEndpoints.deleteBooking(bookingId, token);

        response.then().statusCode(201);
        assertEquals(response.asString(), "Created", "Response body should be 'Created'");

        logger.info("Response Body: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());

        Response getResponse = BookingEndpoints.getBooking(bookingId);
        getResponse.then().statusCode(404);
        assertEquals(getResponse.asString(), "Not Found", "Booking should not be found after deletion");
    }
}
