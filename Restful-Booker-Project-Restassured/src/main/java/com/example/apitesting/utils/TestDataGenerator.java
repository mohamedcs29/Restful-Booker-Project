package com.example.apitesting.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TestDataGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] FIRST_NAMES = {"John", "Jane", "Peter", "Alice", "Bob", "Charlie"};
    private static final String[] LAST_NAMES = {"Doe", "Smith", "Jones", "Williams", "Brown", "Davis"};

    public static String generateRandomFirstName() {

        return FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
    }

    public static String generateRandomLastName() {

        return LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
    }

    public static int generateRandomTotalPrice() {

        return RANDOM.nextInt(1000) + 50;
    }

    public static boolean generateRandomDepositPaid() {

        return RANDOM.nextBoolean();
    }

    public static String generateRandomCheckinDate() {
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ISO_DATE);
    }

    public static String generateRandomCheckoutDate() {
        LocalDate checkin = LocalDate.parse(generateRandomCheckinDate());
        LocalDate checkout = checkin.plusDays(RANDOM.nextInt(10) + 1);
        return checkout.format(DateTimeFormatter.ISO_DATE);
    }
}

