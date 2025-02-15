package pl.where2play.restapie2etest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiTestBase {

    @LocalServerPort
    private int port;

    private RequestSpecification requestSpec;

    @BeforeAll
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        RestAssured.port = port;
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();
    }

    // Test data provider for different HTTP methods and request types
    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                // GET requests
                Arguments.of(
                        "GET",
                        "/api/resources",
                        null,  // no body
                        new HashMap<String, Object>() {{
                            put("page", 0);
                            put("size", 10);
                        }},
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer token");
                        }},
                        null,  // no path params
                        null,  // no multipart
                        HttpStatus.OK.value()
                ),

                // POST with JSON body
                Arguments.of(
                        "POST",
                        "/api/resources",
                        "{\"name\": \"test\", \"value\": \"data\"}",
                        null,  // no query params
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer token");
                        }},
                        null,  // no path params
                        null,  // no multipart
                        HttpStatus.CREATED.value()
                ),

                // PUT with path parameter
                Arguments.of(
                        "PUT",
                        "/api/resources/{id}",
                        "{\"name\": \"updated\"}",
                        null,  // no query params
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer token");
                        }},
                        new HashMap<String, Object>() {{
                            put("id", "123");
                        }},
                        null,  // no multipart
                        HttpStatus.OK.value()
                ),

                // POST with multipart
                Arguments.of(
                        "POST",
                        "/api/upload",
                        null,  // no JSON body
                        null,  // no query params
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer token");
                        }},
                        null,  // no path params
                        new HashMap<String, Object>() {{
                            put("file", new File("test.txt"));
                            put("description", "Test file");
                        }},
                        HttpStatus.OK.value()
                ),

                // DELETE with path parameter
                Arguments.of(
                        "DELETE",
                        "/api/resources/{id}",
                        null,  // no body
                        null,  // no query params
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer token");
                        }},
                        new HashMap<String, Object>() {{
                            put("id", "123");
                        }},
                        null,  // no multipart
                        HttpStatus.NO_CONTENT.value()
                ),

                // PATCH with body and query params
                Arguments.of(
                        "PATCH",
                        "/api/resources",
                        "{\"status\": \"active\"}",
                        new HashMap<String, Object>() {{
                            put("version", "1.0");
                        }},
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer token");
                        }},
                        null,  // no path params
                        null,  // no multipart
                        HttpStatus.OK.value()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testEndpoint(
            String method,
            String path,
            String body,
            Map<String, Object> queryParams,
            Map<String, String> headers,
            Map<String, Object> pathParams,
            Map<String, Object> multipartParams,
            int expectedStatus
    ) {
        // Start building the request
        var request = RestAssured.given(requestSpec);

        // Add headers
        if (headers != null) {
            request.headers(headers);
        }

        // Add query parameters
        if (queryParams != null) {
            request.queryParams(queryParams);
        }

        // Add path parameters
        if (pathParams != null) {
            request.pathParams(pathParams);
        }

        // Add body if present
        if (body != null) {
            request.body(body);
        }

        // Add multipart parameters
        if (multipartParams != null) {
            multipartParams.forEach((key, value) -> {
                if (value instanceof File) {
                    request.multiPart(key, (File) value);
                } else {
                    request.multiPart(key, value);
                }
            });
            request.contentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        }

        // Execute request based on HTTP method
        switch (method.toUpperCase()) {
            case "GET":
                request.get(path)
                        .then()
                        .log().all()
                        .statusCode(expectedStatus);
                break;
            case "POST":
                request.post(path)
                        .then()
                        .statusCode(expectedStatus);
                break;
            case "PUT":
                request.put(path)
                        .then()
                        .statusCode(expectedStatus);
                break;
            case "DELETE":
                request.delete(path)
                        .then()
                        .statusCode(expectedStatus);
                break;
            case "PATCH":
                request.patch(path)
                        .then()
                        .statusCode(expectedStatus);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    // Additional utility methods for response validation
    protected void validateJsonSchema(String response, String schemaPath) {
        // Add JSON schema validation
    }

    protected void validateResponseHeaders(Map<String, String> expectedHeaders) {
        // Add header validation
    }
}
