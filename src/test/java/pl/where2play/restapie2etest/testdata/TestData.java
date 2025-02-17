package pl.where2play.restapie2etest.testdata;

import com.github.javafaker.Faker;
import pl.where2play.restapie2etest.model.User;

import java.util.function.Consumer;

public class TestData {
    private static final Faker faker = new Faker();

    // Generic entity builder with default values
    public static <T> T build(Class<T> clazz, Consumer<T> customizer) {
        T entity = createDefaultInstance(clazz);
        customizer.accept(entity);
        return entity;
    }

    // Base template for all entities
    private static <T> T createDefaultInstance(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            // Common default values for all entities
            if (instance instanceof Identifiable<?> identifiable) {
                setDefaultId(identifiable);
            }

            // Entity-specific initialization
            if (clazz == User.class) {
                initUser((User) instance);
//            } else if (clazz == Product.class) {
//                initProduct((Product) instance);
            }
            // Add other entities here

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Test data creation failed for " + clazz.getSimpleName(), e);
        }
    }

    private static void setDefaultId(Identifiable<?> entity) {
        if (entity.getId() instanceof Long) {
            ((Identifiable<Long>) entity).setId(0L);
        } else if (entity.getId() instanceof String) {
            ((Identifiable<String>) entity).setId("DEFAULT_ID");
        }
    }

    // Entity-specific initializers
    private static void initUser(User user) {
        user.setEmail(faker.internet().emailAddress());
        user.setFirstName(faker.name().firstName());
        user.setRole("USER");
    }

//    private static void initProduct(Product product) {
//        product.setName(faker.commerce().productName());
//        product.setPrice(faker.number().randomDouble(2, 10, 100));
//    }


    // Reusable template for API payloads
    public static User.UserBuilder userTemplate() {
        return User.builder()
                .id("0l")
                .email(faker.internet().emailAddress())
                .role("USER");
    }

    // Ready-to-use test user
    public static User defaultUser() {
        return userTemplate().build();
    }

//    public static Order createOrderWithUser(Consumer<User> userCustomizer) {
//        User user = build(User.class, userCustomizer);
//        return build(Order.class, o -> o.setUser(user));
//    }

//    public class OrderTestHelper {
//        public static Order createOrderWithItems(int itemCount) {
//            Order order = TestData.build(Order.class);
//            for (int i = 0; i < itemCount; i++) {
//                order.addItem(TestData.build(OrderItem.class));
//            }
//            return order;
//        }
//    }

}
