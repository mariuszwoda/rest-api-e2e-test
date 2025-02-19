//package pl.where2play.restapie2etest.teststate;
//
//// Shared state manager
//public class TestState {
//    private static final Map<String, Object> store = new ConcurrentHashMap<>();
//
//    public static void put(String key, Object value) {
//        store.put(key, value);
//    }
//
//    public static <T> T get(String key, Class<T> type) {
//        return type.cast(store.get(key));
//    }
//
//    public static void clear() {
//        store.clear();
//    }
//}
//
//// Base test class with cleanup
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public abstract class StatefulApiTestBase extends ApiTestBase {
//
//    @AfterAll
//    void cleanup() {
//        // Clean up created resources
//        if (TestState.get("createdUserId", String.class) != null) {
//            deleteUser(TestState.get("createdUserId", String.class));
//        }
//    }
//
//    private void deleteUser(String userId) {
//        RestAssured.given(requestSpec)
//                .header("Authorization", "Bearer token")
//                .delete("/users/{id}", userId)
//                .then()
//                .statusCode(HttpStatus.NO_CONTENT.value());
//    }
//}
//
//// Example parameterized test with state sharing
//public class UserFlowTest extends StatefulApiTestBase {
//
//    private static Stream<Arguments> provideUserFlowTestCases() {
//        return Stream.of(
//                Arguments.of("CREATE_USER", null, null),
//                Arguments.of("UPDATE_USER", "createdUserId", "GET_USER"),
//                Arguments.of("DELETE_USER", "createdUserId", null)
//        );
//    }
//
//    @ParameterizedTest(name = "{0}")
//    @MethodSource("provideUserFlowTestCases")
//    @Order(1)
//    void testUserLifecycle(String operation, String idKey, String nextOperation) {
//        switch (operation) {
//            case "CREATE_USER" -> {
//                Response response = RestAssured.given(requestSpec)
//                        .body("""
//                        {
//                            "name": "Test User",
//                            "email": "test@example.com"
//                        }
//                        """)
//                        .post("/users")
//                        .then()
//                        .statusCode(HttpStatus.CREATED.value())
//                        .extract().response();
//
//                String userId = response.path("id");
//                TestState.put("createdUserId", userId);
//            }
//            case "UPDATE_USER" -> {
//                String userId = TestState.get(idKey, String.class);
//                RestAssured.given(requestSpec)
//                        .body("""
//                        {
//                            "email": "updated@example.com"
//                        }
//                        """)
//                        .put("/users/{id}", userId)
//                        .then()
//                        .statusCode(HttpStatus.OK.value());
//            }
//            case "DELETE_USER" -> {
//                String userId = TestState.get(idKey, String.class);
//                RestAssured.given(requestSpec)
//                        .delete("/users/{id}", userId)
//                        .then()
//                        .statusCode(HttpStatus.NO_CONTENT.value());
//                TestState.put("createdUserId", null);
//            }
//        }
//
//        if (nextOperation != null) {
//            // Chain operations within the same test method
//            testUserLifecycle(nextOperation, idKey, null);
//        }
//    }
//
//    @Test
//    @Order(2)
//    void verifyUserDeletion() {
//        String userId = TestState.get("createdUserId", String.class);
//        assertThat(userId).isNull();
//    }
//}
