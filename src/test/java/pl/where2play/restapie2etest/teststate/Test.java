//package pl.where2play.restapie2etest.teststate;
//
//import io.restassured.response.Response;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import java.util.Map;
//import java.util.stream.Stream;
//
//public class Test {
//
//    private static Stream<Arguments> provideOrderTestCases() {
//        return Stream.of(
//                Arguments.of(
//                        "CREATE_ORDER",
//                        null,
//                        Map.of("productId", "prod_123", "quantity", 2),
//                        "PAYMENT"
//                ),
//                Arguments.of(
//                        "PAYMENT",
//                        "orderId",
//                        Map.of("paymentMethod", "credit_card"),
//                        "FULFILLMENT"
//                )
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideOrderTestCases")
//    void testOrderWorkflow(String operation, String idKey, Map<String, Object> params, String nextStep) {
//        switch (operation) {
//            case "CREATE_ORDER" -> {
//                Response response = createOrder(params);
//                TestState.put("orderId", response.path("id"));
//            }
//            case "PAYMENT" -> {
//                String orderId = TestState.get(idKey, String.class);
//                processPayment(orderId, params);
//            }
//        }
//
//        if (nextStep != null) {
//            testOrderWorkflow(nextStep, idKey, params, null);
//        }
//    }
//

/*
// ProductCreationTest.java
public class ProductCreationTest extends StatefulApiTestBase {
    @Test
    void createTestProduct() {
        Response response = RestAssured.given(requestSpec)
            .body(testProduct)
            .post("/products")
            .then()
            .statusCode(201)
            .extract().response();

        TestState.put("testProductId", response.path("id"));
    }
}

// OrderProcessingTest.java
public class OrderProcessingTest extends StatefulApiTestBase {
    @Test
    void createOrderWithSharedProduct() {
        String productId = TestState.get("testProductId", String.class);

        Response response = RestAssured.given(requestSpec)
            .body(Map.of(
                "productId", productId,
                "quantity", 1
            ))
            .post("/orders")
            .then()
            .statusCode(201)
            .extract().response();

        TestState.put("orderId", response.path("id"));
    }
}

 */

/*
private static Stream<Arguments> provideDynamicCases() {
    List<Arguments> args = new ArrayList<>();

    // Initial creation
    args.add(Arguments.of("CREATE", "user", null));

    // Subsequent operations using created ID
    if (TestState.get("user", String.class) != null) {
        args.add(Arguments.of("UPDATE", "user", TestState.get("user", String.class)));
        args.add(Arguments.of("DELETE", "user", TestState.get("user", String.class)));
    }

    return args.stream();
}

 */

/*
//database cleanup
@Sql(scripts = "classpath:cleanup.sql", executionPhase = AFTER_TEST_METHOD)
public class ResourceTests {
    // Tests here
}

 */

/*
state validatation

@AfterEach
void verifyStateConsistency() {
    if (TestState.get("orderId", String.class) != null) {
        RestAssured.given(requestSpec)
            .get("/orders/{id}", TestState.get("orderId", String.class))
            .then()
            .statusCode(200);
    }
}

 */

/*
parallel execution safety

public class TestState {
    private static final ThreadLocal<Map<String, Object>> threadStore =
        ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static void put(String key, Object value) {
        threadStore.get().put(key, value);
    }
}

 */
//}
