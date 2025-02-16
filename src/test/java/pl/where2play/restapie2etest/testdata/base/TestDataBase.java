package pl.where2play.restapie2etest.testdata.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;

import java.util.function.Consumer;

public abstract class TestDataBase<T> {

    protected final Faker faker = new Faker();
    protected final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
            //.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    // Template method for creating default entity
    protected abstract T createDefaultEntity();

    // Generic builder with customization
    public T buildEntity(Consumer<T> customizer) {
        T entity = createDefaultEntity();
        customizer.accept(entity);
        return entity;
    }

    // Generate JSON from entity
    public String toJson(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    // Common utilities
    protected String randomEmail() {
        return faker.internet().emailAddress();
    }

    protected String randomFirstName() {
        return faker.name().firstName();
    }

    protected String randomLastName() {
        return faker.name().lastName();
    }
}
