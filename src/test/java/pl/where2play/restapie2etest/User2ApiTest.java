package pl.where2play.restapie2etest;

import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class User2ApiTest extends ApiTestBase {
    private static final String USERS_PATH = "/users";

    private Stream<Arguments> provideUserTestCases() {
        return Stream.of(
                // Get users with pagination and filtering
                Arguments.of(
                        "GET",
                        USERS_PATH,
                        null,
                        new HashMap<String, Object>() {{
                            put("page", 0);
                            put("size", 10);
                            put("role", "ADMIN");
                            put("status", "ACTIVE");
                        }},
                        getAuthHeaders(),
                        null,
                        null,
                        HttpStatus.OK.value()
                ),

                // Create new user
                Arguments.of(
                        "POST",
                        USERS_PATH,
                        """
                                {
                                    "email": "test@example.com",
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "role": "USER",
                                    "department": "IT"
                                }
                                """,
                        null,
                        getAuthHeaders(),
                        null,
                        null,
                        HttpStatus.CREATED.value()
                ),

                // Update user with path parameter
                Arguments.of(
                        "PUT",
                        USERS_PATH + "/{id}",
                        """
                                {
                                    "firstName": "John Updated",
                                    "lastName": "Doe Updated",
                                    "department": "HR"
                                }
                                """,
                        null,
                        getAuthHeaders(),
                        Map.of("id", "123"),
//                        new HashMap<String, Object>() {{
//                            put("id", "123");
//                        }},
                        null,
                        HttpStatus.OK.value()
                ),

                // Bulk update users
                Arguments.of(
                        "PATCH",
                        USERS_PATH + "/bulk",
                        """
                                {
                                    "status": "INACTIVE",
                                    "userIds": ["123", "456", "789"]
                                }
                                """,
                        null,
                        getAuthHeaders(),
                        null,
                        null,
                        HttpStatus.OK.value()
                )
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
        Response response = executeRequest(method, path, body, queryParams,
                headers, pathParams, multipartParams);

        response.then().statusCode(expectedStatus);

        // Validate response against JSON schema
        if (method.equals("GET") && path.equals(USERS_PATH)) {
            validateJsonSchema(response, "schemas/users-list-schema.json");
        } else if (method.equals("POST") && path.equals(USERS_PATH)) {
            validateJsonSchema(response, "schemas/user-schema.json");
        }
    }
}
