package pl.where2play.restapie2etest.enhanced;

import io.restassured.*;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnhancedApiTestBase {

    @LocalServerPort
    private int port;

    @BeforeAll
    void setupRestAssured() {
        RestAssured.port = port;
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

    /**
     * Validates response with support for different response formats and validation types
     */
    protected void validateResponse(Response response, int expectedStatus,
                                    Object expectedValue) {
        response.then().statusCode(expectedStatus);

        // Handle different expected value types
        if (expectedValue == null) {
            return; // Just validate status code
        }

        String responseBody = response.getBody().asString();

        // Handle different expected value types
        if (expectedValue instanceof Map) {
            // JSON object validation with field-by-field comparison
            validateJsonObject(responseBody, (Map<String, Object>) expectedValue);
        } else if (expectedValue instanceof List) {
            // JSON array validation (exact match or contains)
            validateJsonArray(responseBody, (List<?>) expectedValue);
        } else if (expectedValue instanceof String && ((String) expectedValue).startsWith("regex:")) {
            // Regex validation for the entire response body
            String pattern = ((String) expectedValue).substring(6);
            assertThat(responseBody).matches(pattern);
        } else {
            // Direct comparison
            assertThat(responseBody).isEqualTo(expectedValue.toString());
        }
    }

    /**
     * Validates a JSON object response against expected field values
     */
    private void validateJsonObject(String responseBody, Map<String, Object> expectedFields) {
        try {
            JsonPath jsonPath = new JsonPath(responseBody);

            expectedFields.forEach((path, expected) -> {
                try {
                    Object actual = jsonPath.get(path);
                    validateField(path, actual, expected);
                } catch (Exception e) {
                    fail("Failed to validate path '" + path + "': " + e.getMessage());
                }
            });
        } catch (Exception e) {
            fail("Failed to parse response as JSON object: " + e.getMessage() +
                    "\nResponse body: " + responseBody);
        }
    }

    /**
     * Validates a JSON array response against expected values
     */
    private void validateJsonArray(String responseBody, List<?> expectedList) {
        try {
            // For array responses, we need special handling
            JsonPath jsonPath = new JsonPath(responseBody);
            List<Object> actualList = jsonPath.getList("$");

            if (expectedList.size() == 1 && expectedList.get(0) instanceof String &&
                    ((String)expectedList.get(0)).equals("*any*")) {
                // Just validate it's a non-empty array
                assertThat(actualList).isNotEmpty();
            } else if (expectedList.size() == 1 && expectedList.get(0) instanceof String &&
                    ((String)expectedList.get(0)).startsWith("size:")) {
                // Validate array size
                int expectedSize = Integer.parseInt(
                        ((String)expectedList.get(0)).substring(5)
                );
                assertThat(actualList).hasSize(expectedSize);
            } else if (expectedList.size() == 1 && expectedList.get(0) instanceof String &&
                    ((String)expectedList.get(0)).equals("*contains*")) {
                // Will be handled by next parameter which has items to check
            } else if (expectedList.size() > 0 && expectedList.get(0) instanceof List &&
                    ((List<?>)expectedList.get(0)).size() > 0 &&
                    ((List<?>)expectedList.get(0)).get(0).equals("*contains*")) {
                // Contains validation - check if actual list contains all expected items
                List<?> itemsToCheck = (List<?>) ((List<?>)expectedList.get(0)).get(1);
                for (Object item : itemsToCheck) {
                    assertThat(actualList).contains(item);
                }
            } else {
                // Exact match validation
                assertThat(actualList).containsExactlyElementsOf(expectedList);
            }
        } catch (Exception e) {
            fail("Failed to parse response as JSON array: " + e.getMessage() +
                    "\nResponse body: " + responseBody);
        }
    }

    /**
     * Validates a single field value against expected value
     */
    private void validateField(String path, Object actual, Object expected) {
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
            else if (expectedStr.startsWith("*min:")) {
                double min = Double.parseDouble(expectedStr.substring(5, expectedStr.length() - 1));
                assertThat((Number)actual).isGreaterThanOrEqualTo(min);
            }
            else if (expectedStr.startsWith("*max:")) {
                double max = Double.parseDouble(expectedStr.substring(5, expectedStr.length() - 1));
                assertThat((Number)actual).isLessThanOrEqualTo(max);
            }
            else {
                assertThat(actual).isEqualTo(expected);
            }
        } else if (expected instanceof List) {
            assertThat(actual).isInstanceOf(List.class);
            List<?> actualList = (List<?>) actual;
            List<?> expectedList = (List<?>) expected;

            if (expectedList.size() == 1 && expectedList.get(0) instanceof String &&
                    ((String)expectedList.get(0)).equals("*any*")) {
                // Just validate it's a non-empty array
                assertThat(actualList).isNotEmpty();
            } else if (expectedList.size() == 1 && expectedList.get(0) instanceof String &&
                    ((String)expectedList.get(0)).startsWith("size:")) {
                // Validate array size
                int expectedSize = Integer.parseInt(
                        ((String)expectedList.get(0)).substring(5)
                );
                assertThat(actualList).hasSize(expectedSize);
            } else {
                // Exact match validation
                assertThat(actualList).containsExactlyElementsOf(expectedList);
            }
        } else {
            assertThat(actual).isEqualTo(expected);
        }
    }
}
