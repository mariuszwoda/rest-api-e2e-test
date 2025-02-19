package pl.where2play.restapie2etest.validation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleApiTestBase {

//    @LocalServerPort
//    private int port;

    @BeforeAll
    void setupRestAssured() {
//        RestAssured.port = port;
    }

    protected Response executeRequest(String method, String path, Object body,
                                                 Map<String, Object> params) {
        RequestSpecification request = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer test-token");

        if (body != null) {
            request.body(body);
        }
        if (params != null) {
            request.params(params);
        }

        return switch (method.toUpperCase()) {
            case "GET" -> request.get(path);
            case "POST" -> request.post(path);
            case "PUT" -> request.put(path);
            case "DELETE" -> request.delete(path);
            default -> throw new IllegalArgumentException("Invalid method: " + method);
        };
    }

//    protected void validateResponse1(Response response, int expectedStatus,
//                                    Map<String, Object> expectedFields) {
//        response.then().statusCode(expectedStatus);
//
//        JsonPath jsonPath = response.jsonPath();
//        expectedFields.forEach((path, expectedValue) -> {
//            if (expectedValue instanceof String && ((String) expectedValue).startsWith("regex:")) {
//                String pattern = ((String) expectedValue).substring(6);
//                jsonPath.get(path).toString().matches(pattern);
//            } else {
//                assertThat(jsonPath.get(path)).isEqualTo(expectedValue);
//            }
//        });
//    }

    protected void validateResponse(Response response, int expectedStatus,
                                    Map<String, Object> expectedFields) {
        response.then().statusCode(expectedStatus);

        JsonPath jsonPath = response.jsonPath();
        expectedFields.forEach((path, expected) -> {
            Object actual = jsonPath.get(path);

            if (expected instanceof String) {
                String expectedStr = (String) expected;

                if (expectedStr.startsWith("regex:")) {
                    String pattern = expectedStr.substring(6);
                    assertThat(actual.toString()).matches(pattern);
                }
                else if (expectedStr.equals("*exists*")) {
                    assertThat(actual).isNotNull();
                }
                else if (expectedStr.equals("*number*")) {
                    assertThat(actual).isInstanceOf(Number.class);
                }
                else {
                    assertThat(actual).isEqualTo(expected);
                }
            } else {
                assertThat(actual).isEqualTo(expected);
            }
        });
    }

    //Map.of(
    //    "id", "*exists*",      // Just validate field exists
    //    "count", "*number*",   // Validate numeric type
    //    "email", "regex:.+@.+\\..+" // Validate email pattern
    //)

    protected void validateResponse2(Response response, int expectedStatus,
                                    Map<String, Object> expectedFields) {
        response.then().statusCode(expectedStatus);

        JsonPath jsonPath = response.jsonPath();
        expectedFields.forEach((path, expectedValue) -> {
            if (expectedValue instanceof String && expectedValue.equals("success")) {
                assertThat(jsonPath.get(path)).isEqualTo("success");
            } else {
                assertThat(jsonPath.get(path)).isEqualTo(expectedValue);
            }
        });
    }

}

