package pl.where2play.restapie2etest.testdata.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;

import java.util.Random;
import java.util.UUID;

public final class TestDataBaseStatic {

    public static final Faker FAKER = new Faker(new Random(12345));
    public static final ObjectMapper OBJECT_MAPPER = createConfiguredMapper();

    private static ObjectMapper createConfiguredMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Add Java 8 Date/Time support
        mapper.registerModule(new JavaTimeModule());
        // Add other modules if needed (e.g., JodaTime)
        return mapper;
    }

    // Common field generators
    public static String randomId() {
        return UUID.randomUUID().toString();
    }

    public static String randomEmail() {
        return FAKER.internet().emailAddress();
    }

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    public static String randomLastName() {
        return FAKER.name().lastName();
    }

    // Common JSON serialization
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    // Prevent instantiation
    private TestDataBaseStatic() {}
}
