package org.example;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Main {
    public static void main(String[] args) {
        Response response = RestAssured.given()
                .baseUri("https://api-generator.retool.com/7kbSLy/data/1")
                .log()
                .all()
                .get();

        // Use JSONPath to directly access the "Column 1" property
        JsonPath jsonPath = response.getBody().jsonPath();
        String columnValue = jsonPath.getString("'Column 1'");

        // Output the value
        System.out.println("Value of Column 1: " + columnValue);
    }
}