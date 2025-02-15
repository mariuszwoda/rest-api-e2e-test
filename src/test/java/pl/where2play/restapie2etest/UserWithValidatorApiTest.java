package pl.where2play.restapie2etest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserWithValidatorApiTest extends ApiTestBase {
        private static final String USERS_PATH = "/users";
        private String createdUserId;  // To store created user ID for subsequent tests

        @Test
        void testCreateAndValidateUser() {
            // Create user
            String userPayload = """
            {
                "email": "test@example.com",
                "firstName": "John",
                "lastName": "Doe",
                "role": "USER",
                "department": "IT"
            }
            """;

            Response createResponse = executeRequest(
                    "POST",
                    USERS_PATH,
                    userPayload,
                    null,
                    getAuthHeaders(),
                    null,
                    null
            );

            // Validate creation response
            ResponseValidator validator = validateResponse(createResponse)
                    .validateStatusCode(HttpStatus.CREATED.value())
                    .validateFieldExists("id")
                    .validateField("email", "test@example.com")
                    .validateField("firstName", "John")
                    .validateField("lastName", "Doe")
                    .validateFieldPattern("createdAt", "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*");

            // Store created user ID for later use
            createdUserId = validator.extractField("id", String.class);

            // Get created user
            Response getResponse = executeRequest(
                    "GET",
                    USERS_PATH + "/" + createdUserId,
                    null,
                    null,
                    getAuthHeaders(),
                    null,
                    null
            );

            // Validate get response
            validateResponse(getResponse)
                    .validateStatusCode(HttpStatus.OK.value())
                    .validateField("id", createdUserId)
                    .validateField("email", "test@example.com")
                    .validateCustom(response -> {
                        // Custom complex validation
                        JsonPath jsonPath = response.jsonPath();
                        assertThat(jsonPath.getString("email")).contains("@");
                        assertThat(jsonPath.getString("firstName")).hasSize(4);
                    });
        }

        @Test
        void testUsersList() {
            Response response = executeRequest(
                    "GET",
                    USERS_PATH,
                    null,
                    Map.of("page", 0, "size", 10),
                    getAuthHeaders(),
                    null,
                    null
            );

            validateResponse(response)
                    .validateStatusCode(HttpStatus.OK.value())
                    .validateFieldExists("content")
                    .validateArrayNotEmpty("content")
                    .validateField("pageable.pageSize", 10)
                    .validateCustom(r -> {
                        List<String> emails = r.jsonPath().getList("content.email");
                        assertThat(emails).allMatch(email -> email.contains("@"));
                    });
        }
    }
