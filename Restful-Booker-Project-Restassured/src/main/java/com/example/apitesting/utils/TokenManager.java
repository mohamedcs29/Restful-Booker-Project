package com.example.apitesting.utils;

import com.example.apitesting.api.endpoints.AuthEndpoints;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TokenManager {

    private static final Logger logger = LogManager.getLogger(TokenManager.class);
    private static String token;
    private static long tokenExpiryTime;

    public static String getToken() {
        if (token == null || isTokenExpired()) {
            generateNewToken();
        }
        return token;
    }

    private static boolean isTokenExpired() {
        return System.currentTimeMillis() > tokenExpiryTime - (5 * 60 * 1000);
    }

    private static void generateNewToken() {
        logger.info("Generating new authentication token...");

        Response response = AuthEndpoints.createToken(ConfigManager.getProperty("auth.username"), ConfigManager.getProperty("auth.password"));
        if (response.getStatusCode() == 200) {
            token = response.jsonPath().getString("token");
            tokenExpiryTime = System.currentTimeMillis() + (25 * 60 * 1000);
            logger.info("New token generated successfully.");
        } else {
            logger.error("Failed to generate new token. Status code: " + response.getStatusCode() + ", Response: " + response.asString());
            throw new RuntimeException("Failed to generate authentication token");
        }
    }
}

