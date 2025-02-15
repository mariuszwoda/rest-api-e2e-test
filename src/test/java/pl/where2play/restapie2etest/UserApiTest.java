package pl.where2play.restapie2etest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class UserApiTest extends ApiTestBase {

    private Stream<Arguments> provideUserTestCases() {
        return Stream.of(
                // Specific test cases for UserController
                Arguments.of(
                        "GET",
                        "/api/users",
                        null,
                        new HashMap<String, Object>() {{
                            put("role", "admin");
                        }},
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer token");
                        }},
                        null,
                        null,
                        HttpStatus.OK.value()
                )
                // Add more test cases specific to UserController
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserTestCases")
    void testUserEndpoints(String method, String path, String body,
                           Map<String, Object> queryParams,
                           Map<String, String> headers,
                           Map<String, Object> pathParams,
                           Map<String, Object> multipartParams,
                           int expectedStatus) {
        super.testEndpoint(method, path, body, queryParams, headers,
                pathParams, multipartParams, expectedStatus);
    }
}
