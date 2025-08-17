package com.example.apitesting.api.endpoints;

import com.example.apitesting.api.RestClient;
import com.example.apitesting.models.Booking;
import io.restassured.response.Response;

public class BookingEndpoints {

    private static final String BOOKING_ENDPOINT = "/booking";

    public static Response createBooking(Booking bookingPayload) {
        return RestClient.post(BOOKING_ENDPOINT, bookingPayload);
    }

    public static Response getBooking(int bookingId) {

        return RestClient.get(BOOKING_ENDPOINT + "/" + bookingId);
    }

    public static Response getAllBookings() {

        return RestClient.get(BOOKING_ENDPOINT);
    }

    public static Response getBookingByNames(String firstName, String lastName) {
        return RestClient.get(BOOKING_ENDPOINT + "?firstname=" + firstName + "&lastname=" + lastName);
    }

    public static Response getBookingByDates(String checkin, String checkout) {
        return RestClient.get(BOOKING_ENDPOINT + "?checkin=" + checkin + "&checkout=" + checkout);
    }

    public static Response updateBooking(int bookingId, Booking bookingPayload, String token) {
        return RestClient.put(BOOKING_ENDPOINT + "/" + bookingId, bookingPayload, token);
    }

    public static Response deleteBooking(int bookingId, String token) {
        return RestClient.delete(BOOKING_ENDPOINT + "/" + bookingId, token);
    }

    public static Response partialUpdateBooking(int bookingId, Object partialBookingPayload, String token) {
        return RestClient.patch(BOOKING_ENDPOINT + "/" + bookingId, partialBookingPayload, token);
    }
}

