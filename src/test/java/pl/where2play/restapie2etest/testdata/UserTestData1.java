package pl.where2play.restapie2etest.testdata;

import com.github.javafaker.Faker;
import pl.where2play.restapie2etest.model.User;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class UserTestData1 {

//    private static final Faker faker = new Faker();
    private static final Faker faker = new Faker(new Random(12345));

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

    // Generate JSON from a user entity
    public static String userJson(User user) {
        return """
            {
                "id": "%s",
                "email": "%s",
                "firstName": "%s",
                "lastName": "%s",
                "role": "%s",
                "department": "%s"
            }
            """.formatted(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getDepartment()
        );
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
