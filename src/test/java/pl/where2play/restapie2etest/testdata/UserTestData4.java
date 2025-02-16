package pl.where2play.restapie2etest.testdata;

import pl.where2play.restapie2etest.model.User;
import pl.where2play.restapie2etest.testdata.base.TestDataBase;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserTestData4 extends TestDataBase<User> {

    @Override
    protected User createDefaultEntity() {
        return new User(
                UUID.randomUUID().toString(),
                randomEmail(),
                randomFirstName(),
                randomLastName(),
                "USER",
                faker.commerce().department(),
                LocalDateTime.now()
        );
    }

    // Predefined user types
    public User defaultUser() {
        return buildEntity(u -> {
        });
    }

    public User admin() {
        return buildEntity(u -> u.setRole("ADMIN"));
    }

    public User withDepartment(String department) {
        return buildEntity(u -> u.setDepartment(department));
    }

    // Shortcut for default user JSON
    public String defaultUserJson() {
        return toJson(buildEntity(u -> {
        }));
    }
}
