package pl.where2play.restapie2etest.validation;

import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

public class ExampleControllerTest extends SimpleApiTestBase {

    private static Stream<Arguments> responseTestCases() {
        return Stream.of(
                // Example of a successful response
                Arguments.of(
                        "GET",
                        "/example/success",
                        null, // No request body
                        null, // No query params
                        200, // Expected status
                        Map.of( // Expected response fields
                                "result", "success"
                        )
                ),

                // Example of another successful response
                Arguments.of(
                        "POST",
                        "/example/submit",
                        Map.of("data", "test"), // Request body
                        null, // No query params
                        201, // Expected status
                        Map.of(
                                "status", "success"
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("responseTestCases")
    void testExampleEndpoints(String method, String path, Object requestBody,
                              Map<String, Object> params, int expectedStatus,
                              Map<String, Object> expectedFields) {

        Response response = executeRequest(method, path, requestBody, params);
        validateResponse2(response, expectedStatus, expectedFields);
    }
}
