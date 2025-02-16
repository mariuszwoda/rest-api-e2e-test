package pl.where2play.restapie2etest.testdata;

import pl.where2play.restapie2etest.model.User;
import pl.where2play.restapie2etest.testdata.base.TestDataBaseStatic;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static pl.where2play.restapie2etest.testdata.base.TestDataBaseStatic.*;
import static pl.where2play.restapie2etest.testdata.base.TestDataBaseStatic.toJson;

public class UserTestDataStatic {

    // Core builder
    private static User buildUser(Consumer<User> customizer) {
        User user = new User(
                randomId(),
                randomEmail(),
                randomFirstName(),
                randomLastName(),
                "USER",
                FAKER.commerce().department(),
                LocalDateTime.now()
        );
        customizer.accept(user);
        return user;
    }

    // Static factory methods
    public static User user() {
        return buildUser(u -> {
        });
    }

    public static User admin() {
        return buildUser(u -> u.setRole("ADMIN"));
    }

    public static User withDepartment(String department) {
        return buildUser(u -> u.setDepartment(department));
    }

    // JSON shortcuts
    public static String defaultUserJson() {
        return toJson(user());
    }

    public static String adminJson() {
        return toJson(admin());
    }
}
