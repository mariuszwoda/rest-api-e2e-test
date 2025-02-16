package pl.where2play.restapie2etest.testdata;

import com.github.javafaker.Faker;
import pl.where2play.restapie2etest.model.User;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class UserTestData2 {

    private static final Faker faker = new Faker();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Base method for creating a user entity
    public static User user() {
        return user(u -> {}); // Default user with no overrides
    }

    // Overridable method for creating a user entity
    public static User user(Consumer<User> customizer) {
        User user = new User(
                UUID.randomUUID().toString(),
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.name().lastName(),
                "USER",
                faker.commerce().department(),
                LocalDateTime.now()
        );
        customizer.accept(user); // Apply customizations
        return user;
    }

    // Predefined user types
    public static User admin() {
        return user(u -> u.setRole("ADMIN"));
    }

    public static User withDepartment(String department) {
        return user(u -> u.setDepartment(department));
    }

    // Generate JSON dynamically from a user entity
    public static String userJson(User user) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("id", user.getId());
        jsonMap.put("email", user.getEmail());
        jsonMap.put("firstName", user.getFirstName());
        jsonMap.put("lastName", user.getLastName());
        jsonMap.put("role", user.getRole());
        jsonMap.put("department", user.getDepartment());

        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JSON", e);
        }
    }

    // Generate JSON for a default user
    public static String defaultUserJson() {
        return userJson(user());
    }

    // Generate JSON for an admin user
    public static String adminUserJson() {
        return userJson(admin());
    }
}

