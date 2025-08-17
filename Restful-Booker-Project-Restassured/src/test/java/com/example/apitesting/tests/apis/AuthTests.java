package com.example.apitesting.tests.apis;

import com.example.apitesting.api.endpoints.AuthEndpoints;
import com.example.apitesting.base.BaseTest;
import com.example.apitesting.models.Auth;
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
import static org.testng.Assert.assertEquals;

@Epic("API Regression Testing")
@Feature("Authentication API")
public class AuthTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(AuthTests.class);

    @Test(description = "Verify successful token creation")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Create authentication token with valid credentials")
    public void testCreateToken() {
        Auth authPayload = new Auth("admin", "password123");

        Response response = AuthEndpoints.createToken(authPayload);

        response.then()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/authSchema.json"));

        String token = response.jsonPath().getString("token");
        assertNotNull(token, "Token should not be null");
        assertEquals(token.length() > 0, true, "Token should not be empty");

        logger.info("Auth Token: " + token);
        logger.info("Response Body: " + response.asString());
        io.qameta.allure.Allure.addAttachment("Response Body", "application/json", response.asString());
    }


}

