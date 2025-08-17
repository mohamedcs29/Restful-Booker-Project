package com.example.apitesting.tests.apis;

import com.example.apitesting.api.endpoints.BookingEndpoints;
import com.example.apitesting.base.BaseTest;
import com.example.apitesting.models.Booking;
import com.example.apitesting.models.BookingDates;
import com.example.apitesting.utils.TestDataGenerator;
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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Epic("API Regression Testing")
@Feature("Booking API")
public class SearchBookingTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(SearchBookingTests.class);
    private String firstName = "TestFirstName";
    private String lastName = "TestLastName";
    private String checkinDate = "2025-01-01";
    private String checkoutDate = "2025-01-05";

    @BeforeClass
    public void setupTestData() {
        BookingDates bookingDates = new BookingDates(checkinDate, checkoutDate);
        Booking bookingPayload = new Booking(firstName, lastName, 150, true, bookingDates, "Breakfast");
        Response createResponse = BookingEndpoints.createBooking(bookingPayload);
        createResponse.then().statusCode(200);
        logger.info("Setup: Created booking for search tests with firstname: " + firstName + ", lastname: " + lastName + ", checkin: " + checkinDate + ", checkout: " + checkoutDate);
    }

    @Test(description = "Verify searching bookings by name")
    @Severity(SeverityLevel.NORMAL)
    @Step("Search for bookings using first name and last name filters")
    public void testSearchBookingByName() {
        Response response = BookingEndpoints.getBookingByNames(firstName, lastName);

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/searchBookingSchema.json"));


        assertNotNull(response.jsonPath().getList("bookingid"), "Booking IDs list should not be null");

        logger.info("Search by Name Results: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }

    @Test(description = "Verify searching bookings by dates")
    @Severity(SeverityLevel.NORMAL)
    @Step("Search for bookings using checkin and checkout date filters")
    public void testSearchBookingByDates() {
        Response response = BookingEndpoints.getBookingByDates(checkinDate, checkoutDate);

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/searchBookingSchema.json"));

        assertNotNull(response.jsonPath().getList("bookingid"), "Booking IDs list should not be null");

        logger.info("Search by Dates Results: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }
}

