package pl.where2play.restapie2etest;

//import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

//import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MultipartApiTestBase {

    @LocalServerPort
    private int port;

    @BeforeAll
    void setupRestAssured() {
        RestAssured.port = port;
    }

    /**
     * Execute a regular REST request
     */
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
     * Execute a multipart request with files
     */
    protected Response executeMultipartRequest(String path, String requestDataJson,
                                               List<TestFile> files) {
        RequestSpecification request = RestAssured.given()
                .header("Authorization", "Bearer test-token");

        // Add the JSON request data as a part
        request.multiPart("requestData", requestDataJson, "application/json");

        // Add files if provided
        if (files != null && !files.isEmpty()) {
            for (TestFile file : files) {
                request.multiPart("file", file.getFileName(), file.getContent(), file.getContentType());
            }
        }

        return request.post(path);
    }

    /**
     * Validates response against expected fields
     */
    protected void validateResponse(Response response, int expectedStatus,
                                    Map<String, Object> expectedFields) {
        response.then().statusCode(expectedStatus);

        if (expectedFields == null || expectedFields.isEmpty()) {
            return; // Just validate status code
        }

        // Special case for array validation
        if (expectedFields.containsKey("$array")) {
            validateArrayResponse(response, expectedFields.get("$array"));
            return;
        }

        // Special case for full response validation
        if (expectedFields.containsKey("$body")) {
            validateFullResponse(response, expectedFields.get("$body"));
            return;
        }

        // Regular JSON object validation
        try {
            JsonPath jsonPath = response.jsonPath();

            expectedFields.forEach((path, expected) -> {
                try {
                    Object actual = jsonPath.get(path);
                    validateField(path, actual, expected);
                } catch (Exception e) {
                    fail("Failed to validate path '" + path + "': " + e.getMessage() +
                            "\nResponse: " + response.getBody().asString());
                }
            });
        } catch (Exception e) {
            fail("Failed to parse response as JSON: " + e.getMessage() +
                    "\nResponse: " + response.getBody().asString());
        }
    }

    /**
     * Validates a JSON array response
     */
    private void validateArrayResponse(Response response, Object arraySpec) {
        String body = response.getBody().asString();

        try {
            List<Object> actualArray = response.jsonPath().getList("$");

            if (arraySpec instanceof List) {
                // Exact array match
                List<?> expectedArray = (List<?>) arraySpec;
                assertThat(actualArray).containsExactlyElementsOf(expectedArray);
            } else if (arraySpec instanceof String) {
                String spec = (String) arraySpec;
                if (spec.startsWith("size:")) {
                    // Size validation
                    int expectedSize = Integer.parseInt(spec.substring(5));
                    assertThat(actualArray).hasSize(expectedSize);
                } else if (spec.equals("*any*")) {
                    // Non-empty validation
                    assertThat(actualArray).isNotEmpty();
                }
            } else if (arraySpec instanceof Map) {
                Map<String, Object> arrayOptions = (Map<String, Object>) arraySpec;

                // Contains validation
                if (arrayOptions.containsKey("contains")) {
                    List<?> itemsToContain = (List<?>) arrayOptions.get("contains");
                    for (Object item : itemsToContain) {
                        assertThat(actualArray).contains(item);
                    }
                }

                // Size validation
                if (arrayOptions.containsKey("size")) {
                    int expectedSize = (int) arrayOptions.get("size");
                    assertThat(actualArray).hasSize(expectedSize);
                }

                // Element validation
                if (arrayOptions.containsKey("elements")) {
                    Map<Integer, Object> elementValidations = (Map<Integer, Object>) arrayOptions.get("elements");
                    elementValidations.forEach((index, expected) -> {
                        if (index >= actualArray.size()) {
                            fail("Array index out of bounds: " + index + ", size: " + actualArray.size());
                        }
                        validateField("[" + index + "]", actualArray.get(index), expected);
                    });
                }

                // Field validation for each element
                if (arrayOptions.containsKey("each")) {
                    Map<String, Object> fieldValidations = (Map<String, Object>) arrayOptions.get("each");
                    for (int i = 0; i < actualArray.size(); i++) {
                        Object element = actualArray.get(i);
                        if (!(element instanceof Map)) {
                            fail("Array element at index " + i + " is not a JSON object");
                        }

                        // Create a JsonPath for this element to validate nested fields
                        JsonPath elementPath = JsonPath.from(new Gson().toJson(element));
                        fieldValidations.forEach((fieldPath, expected) -> {
                            try {
                                Object actual = elementPath.get(fieldPath);
                                validateField("[" + i + "]." + fieldPath, actual, expected);
                            } catch (Exception e) {
                                fail("Failed to validate path '" + "[" + i + "]." + fieldPath + "': " + e.getMessage());
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            fail("Failed to validate array response: " + e.getMessage() +
                    "\nResponse: " + body);
        }
    }

    /**
     * Validates the entire response body
     */
    private void validateFullResponse(Response response, Object expected) {
        String body = response.getBody().asString();

        if (expected instanceof String) {
            String expectedStr = (String) expected;
            if (expectedStr.startsWith("regex:")) {
                String pattern = expectedStr.substring(6);
                assertThat(body).matches(pattern);
            } else {
                assertThat(body).isEqualTo(expectedStr);
            }
        } else {
            assertThat(body).isEqualTo(expected.toString());
        }
    }

    /**
     * Validates a single field value against expected value
     */
    private void validateField(String path, Object actual, Object expected) {
        if (actual == null) {
            if (expected instanceof String && ((String) expected).equals("*null*")) {
                return; // Explicitly expecting null
            }
            fail("Field at path '" + path + "' is null");
        }

        if (expected instanceof String) {
            String expectedStr = (String) expected;

            if (expectedStr.startsWith("regex:")) {
                String pattern = expectedStr.substring(6);
                assertThat(actual.toString()).matches(pattern);
            }
            else if (expectedStr.equals("*exists*")) {
                // Already validated by reaching this point
            }
            else if (expectedStr.equals("*number*")) {
                assertThat(actual).isInstanceOf(Number.class);
            }
            else if (expectedStr.startsWith("*min:")) {
                double min = Double.parseDouble(expectedStr.substring(5, expectedStr.length() - 1));
                assertThat(((Number)actual).doubleValue()).isGreaterThanOrEqualTo(min);
                assertThat(actual).isInstanceOf(Number.class);
            }
            else if (expectedStr.startsWith("*max:")) {
                double max = Double.parseDouble(expectedStr.substring(5, expectedStr.length() - 1));
                assertThat(((Number)actual).doubleValue()).isLessThanOrEqualTo(max);
            }
            else {
                assertThat(actual).isEqualTo(expected);
            }
        } else if (expected instanceof List) {
            assertThat(actual).isInstanceOf(List.class);
//            List<?> actualList = (List<?>) actual;
//            List<?> expectedList = (List<?>) expected;
            List<String> actualList = (List<String>) actual;
            List<String> expectedList = (List<String>) expected;

            assertThat(actualList).containsExactlyElementsOf(expectedList);
        } else {
            assertThat(actual).isEqualTo(expected);
        }
    }

    /**
     * Helper class for test file data
     */
    public static class TestFile {
        private final String fileName;
        private final byte[] content;
        private final String contentType;

        public TestFile(String fileName, byte[] content, String contentType) {
            this.fileName = fileName;
            this.content = content;
            this.contentType = contentType;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getContentType() {
            return contentType;
        }

        // Helper methods to create test files
        public static TestFile createTextFile(String fileName, String content) {
            return new TestFile(fileName, content.getBytes(StandardCharsets.UTF_8), "text/plain");
        }

        public static TestFile createJsonFile(String fileName, String jsonContent) {
            return new TestFile(fileName, jsonContent.getBytes(StandardCharsets.UTF_8), "application/json");
        }

        public static TestFile createPdfFile(String fileName, byte[] content) {
            return new TestFile(fileName, content, "application/pdf");
        }
    }
}
