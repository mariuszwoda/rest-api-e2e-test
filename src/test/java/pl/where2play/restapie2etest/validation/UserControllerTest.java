package pl.where2play.restapie2etest.validation;

import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

// Example test class
public class UserControllerTest extends SimpleApiTestBase {

    private static Stream<Arguments> userTestCases() {
        return Stream.of(
                // POST - Create user
                Arguments.of(
                        "POST",
                        "/users",
                        Map.of( // Request body
                                "name", "John Doe",
                                "email", "john@example.com"
                        ),
                        null, // No query params
                        201, // Expected status
                        Map.of( // Expected response fields
                                "name", "John Doe",
                                "email", "john@example.com",
                                "id", "regex:[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}"
                        )
                ),

                // GET - Get user
                Arguments.of(
                        "GET",
                        "/users/{id}",
                        null, // No request body
                        Map.of("id", "123"), // Path param
                        200,
                        Map.of(
                                "id", "123",
                                "status", "ACTIVE",
                                "createdAt", "regex:\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*"
                        )
                ),

                // PUT - Update user
                Arguments.of(
                        "PUT",
                        "/users/456",
                        Map.of("status", "INACTIVE"),
                        null,
                        200,
                        Map.of(
                                "status", "INACTIVE",
                                "updatedAt", "regex:.*"
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("userTestCases")
    void testUserEndpoints(String method, String path, Object requestBody,
                           Map<String, Object> params, int expectedStatus,
                           Map<String, Object> expectedFields) {

        Response response = executeRequest(method, path, requestBody, params);
        validateResponse(response, expectedStatus, expectedFields);
    }
}
