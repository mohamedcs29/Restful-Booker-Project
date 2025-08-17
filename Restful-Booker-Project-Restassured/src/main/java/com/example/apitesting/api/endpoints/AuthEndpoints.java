package com.example.apitesting.api.endpoints;

import com.example.apitesting.api.RestClient;
import com.example.apitesting.models.Auth;
import io.restassured.response.Response;

public class AuthEndpoints {

    private static final String AUTH_ENDPOINT = "/auth";

    public static Response createToken(String username, String password) {
        Auth authPayload = new Auth(username, password);
        return RestClient.post(AUTH_ENDPOINT, authPayload);
    }


    public static Response createToken(Auth authPayload) {

        return RestClient.post(AUTH_ENDPOINT, authPayload);
    }
}

