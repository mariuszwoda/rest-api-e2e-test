//package pl.where2play.restapie2etest.testdata.base;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.github.javafaker.Faker;
//
//import java.util.Random;
//
//public final class TestDataConfig {
//    // Thread-safe singleton instances
//    public static final Faker FAKER = new Faker(new Random(12345));
//    public static final ObjectMapper OBJECT_MAPPER = createConfiguredMapper();
//
//    private TestDataConfig() {
//        throw new AssertionError("No TestDataConfig instances for you!");
//    }
//
//    private static ObjectMapper createConfiguredMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        // Add Java 8 Date/Time support
//        mapper.registerModule(new JavaTimeModule());
//        // Add other modules if needed (e.g., JodaTime)
//        return mapper;
//    }
//}
