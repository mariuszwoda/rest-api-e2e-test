package pl.where2play.restapie2etest.enhanced;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EnhancedControllerTest extends EnhancedApiTestBase {

    private static Stream<Arguments> testCases() {
        return Stream.of(
                // Test case 1: JSON array response with exact match
                Arguments.of(
                        "GET",
                        "/api/preferences",
                        null,
                        null,
                        200,
                        List.of("test1", "test2", "/userPref")  // Exact array match
                ),

                // Test case 2: JSON array with size validation
                Arguments.of(
                        "GET",
                        "/api/tags",
                        null,
                        null,
                        200,
                        List.of("size:5")  // Expect array with 5 elements
                ),

                // Test case 3: JSON array with contains validation
                Arguments.of(
                        "GET",
                        "/api/permissions",
                        null,
                        null,
                        200,
                        List.of(List.of("*contains*", List.of("READ", "WRITE")))  // Array contains these values
                ),

                // Test case 4: JSON object with field validation
                Arguments.of(
                        "POST",
                        "/api/users",
                        Map.of("name", "John", "email", "john@example.com"),
                        null,
                        201,
                        Map.of(
                                "id", "regex:[a-f0-9-]{36}",
                                "name", "John",
                                "email", "john@example.com",
                                "roles", List.of("USER")
                        )
                ),

                // Test case 5: Nested JSON validation
                Arguments.of(
                        "GET",
                        "/api/products/123",
                        null,
                        null,
                        200,
                        Map.of(
                                "id", "123",
                                "name", "*exists*",
                                "price", "*min:10.0*",
                                "category.id", "CAT-5",
                                "category.name", "Electronics",
                                "tags", List.of("*any*")  // Just validate tags exist and non-empty
                        )
                ),

                // Test case 6: Regex for entire response
                Arguments.of(
                        "GET",
                        "/api/version",
                        null,
                        null,
                        200,
                        "regex:\\d+\\.\\d+\\.\\d+"  // Validate version format
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testEndpoints(String method, String path, Object requestBody,
                       Map<String, Object> params, int expectedStatus,
                       Object expectedValue) {

        Response response = executeRequest(method, path, requestBody, params);
        validateResponse(response, expectedStatus, expectedValue);
    }

    // Example of a specific test with array response validation
    @Test
    void testPreferencesEndpoint() {
        Response response = executeRequest("GET", "/api/preferences", null, null);

        // Different ways to validate the same array response
        validateResponse(response, 200, List.of("test1", "test2", "/userPref"));

        // Alternative: validate it contains specific elements
        validateResponse(response, 200,
                List.of(List.of("*contains*", List.of("test1", "/userPref")))
        );

        // Alternative: validate only the size
        validateResponse(response, 200, List.of("size:3"));
    }
}

