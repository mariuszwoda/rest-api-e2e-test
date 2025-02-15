package pl.where2play.restapie2etest.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pl.where2play.restapie2etest.util.TestTokenProvider;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Disabled
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiTestBase {

//    @LocalServerPort
//    private int port;

    private RequestSpecification requestSpec;

    @BeforeAll
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

//        RestAssured.port = port;
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
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
    protected void testEndpoint(
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
//                        .log().all()
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


    protected static Map<String, String> getAuthHeaders() {
        return new HashMap<String, String>() {{
            put("Authorization", "Bearer " + TestTokenProvider.getTestToken());
        }};
    }

    protected Response executeRequest(String method, String path, String body,
                                      Map<String, Object> queryParams,
                                      Map<String, String> headers,
                                      Map<String, Object> pathParams,
                                      Map<String, Object> multipartParams) {
        RequestSpecification request = RestAssured.given(requestSpec);

        if (headers != null) request.headers(headers);
        if (queryParams != null) request.queryParams(queryParams);
        if (pathParams != null) request.pathParams(pathParams);
        if (body != null) request.body(body);

        if (multipartParams != null) {
            multipartParams.forEach((key, value) -> {
                if (value instanceof File) {
                    request.multiPart(key, (File) value);
                } else if (value instanceof String) {
                    request.multiPart(key, value.toString());
                } else {
                    request.multiPart(key, value);
                }
            });
            request.contentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        }

        return switch (method.toUpperCase()) {
            case "GET" -> request.get(path);
            case "POST" -> request.post(path);
            case "PUT" -> request.put(path);
            case "DELETE" -> request.delete(path);
            case "PATCH" -> request.patch(path);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    protected void validateResponseBody(String expectedJson) {
        RestAssured.given(requestSpec)
                .when()
                .get("/path")
                .then()
                .body(equalTo(expectedJson));
    }

    // Additional utility methods for response validation
    protected void validateJsonSchema(String endpoint, String schemaPath) {
        RestAssured.given(requestSpec)
                .when()
                .get(endpoint)
                .then()
                .assertThat();
//                .body(matchesJsonSchemaInClasspath(schemaPath));
    }

    protected void validateJsonSchema(Response response, String schemaPath) {
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }


    protected void validateResponseHeaders(Map<String, String> expectedHeaders) {
        // Add header validation
    }

    protected void validateCustomResponse(Response response,
                                          Consumer<Response> validationLogic) {
        validationLogic.accept(response);
    }


    protected static class ResponseValidator {
        private final Response response;
        private final JsonPath jsonPath;

        public ResponseValidator(Response response) {
            this.response = response;
            this.jsonPath = response.jsonPath();
        }

        public ResponseValidator validateStatusCode(int expectedStatus) {
            response.then().statusCode(expectedStatus);
            return this;
        }

        public ResponseValidator validateField(String jsonPath, Object expectedValue) {
            response.then().body(jsonPath, equalTo(expectedValue));
            return this;
        }

        public ResponseValidator validateFieldExists(String jsonPath) {
            response.then().body(jsonPath, notNullValue());
            return this;
        }

        public ResponseValidator validateFieldPattern(String jsonPath, String pattern) {
            response.then().body(jsonPath, matchesPattern(pattern));
            return this;
        }

        public ResponseValidator validateArraySize(String jsonPath, int expectedSize) {
            response.then().body(jsonPath + ".size()", equalTo(expectedSize));
            return this;
        }

        public ResponseValidator validateArrayMinSize(String jsonPath, int minSize) {
            response.then().body(jsonPath + ".size()", greaterThanOrEqualTo(minSize));
            return this;
        }

        public ResponseValidator validateArrayNotEmpty(String jsonPath) {
            response.then().body(jsonPath + ".size()", greaterThan(0));
            return this;
        }

        public <T> ResponseValidator validateList(String jsonPath, List<T> expectedList) {
            response.then().body(jsonPath, hasItems(expectedList.toArray()));
            return this;
        }

        public ResponseValidator validateCustom(Consumer<Response> customValidation) {
            customValidation.accept(response);
            return this;
        }

        public <T> T extractField(String jsonPath, Class<T> type) {
            return this.jsonPath.get(jsonPath);
        }

        public Response getResponse() {
            return response;
        }
    }

    protected ResponseValidator validateResponse(Response response) {
        return new ResponseValidator(response);
    }
}
