package com.example.apitesting.api;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

public class RestClient {

    private static final Logger logger = LogManager.getLogger(RestClient.class);

    public static Response get(String path) {
        logger.info("Sending GET request to: " + path);
        Response response = given().log().all().get(path);
        logger.info("Received GET response: " + response.asString());
        return response;
    }
    public static Response post(String path, Object body) {
        logger.info("Sending POST request to: " + path + " with body: " + body);
        Response response =
                        given()
                        .contentType("application/json")
                        .body(body)
                        .log()
                        .all()
                        .post(path);
        logger.info("Received POST response: " + response.asString());
        return response;
    }

    public static Response put(String path, Object body, String token) {
        logger.info("Sending PUT request to: " + path + " with body: " + body + " and token: " + token);
        Response response = given().contentType("application/json").cookie("token", token).body(body).log().all().put(path);
        logger.info("Received PUT response: " + response.asString());
        return response;
    }



    public static Response delete(String path, String token) {
        logger.info("Sending DELETE request to: " + path + " with token: " + token);
        Response response = given().contentType("application/json").cookie("token", token).log().all().delete(path);
        logger.info("Received DELETE response: " + response.asString());
        return response;
    }


    public static Response patch(String path, Object body, String token) {
        logger.info("Sending PATCH request to: " + path + " with body: " + body + " and token: " + token);
        Response response = given().contentType("application/json").cookie("token", token).body(body).log().all().patch(path);
        logger.info("Received PATCH response: " + response.asString());
        return response;
    }




}

