package com.example.apitesting.base;

import com.example.apitesting.utils.ConfigManager;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    @BeforeSuite
    public void setup() {

        RestAssured.baseURI = ConfigManager.getProperty("base.url");
    }
}

