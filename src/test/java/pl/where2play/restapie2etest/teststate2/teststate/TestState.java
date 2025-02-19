/*
@TestConfiguration
public class TestDataHolder {
    private final ConcurrentHashMap<String, Object> testData = new ConcurrentHashMap<>();

    public void store(String key, Object value) {
        testData.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(testData.get(key));
    }

    public void clear() {
        testData.clear();
    }
}
 */

/*
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestDataHolder.class)
public abstract class ApiTestBase {

    @Autowired
    protected TestDataHolder testDataHolder;

    @LocalServerPort
    private int port;

    protected RequestSpecification requestSpec;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();
    }

    @AfterAll
    void tearDown() {
        // Optional: Cleanup after all tests
        // testDataHolder.clear();
    }
}

 */

/*
public class EntityCreationTest extends ApiTestBase {

    @ParameterizedTest
    @MethodSource("provideEntityCreationCases")
    void createEntities(String method, String path, String body, int expectedStatus, String storageKey) {
        Response response = executeRequest(method, path, body, null,
                getAuthHeaders(), null, null);

        response.then().statusCode(expectedStatus);

        // Extract and store ID
        String id = response.jsonPath().getString("id");
        testDataHolder.store(storageKey, id);
    }

    private static Stream<Arguments> provideEntityCreationCases() {
        return Stream.of(
            Arguments.of(
                "POST",
                "/api/users",
                """
                {
                    "name": "Test User",
                    "email": "user@test.com"
                }
                """,
                HttpStatus.CREATED.value(),
                "userId"
            ),
            Arguments.of(
                "POST",
                "/api/products",
                """
                {
                    "name": "Test Product",
                    "price": 99.99
                }
                """,
                HttpStatus.CREATED.value(),
                "productId"
            )
        );
    }
}

 */

/*
public class EntityDependentTest extends ApiTestBase {

    @Test
    void testWithSharedIds() {
        String userId = testDataHolder.get("userId", String.class);
        String productId = testDataHolder.get("productId", String.class);

        // Use the IDs in subsequent tests
        given(requestSpec)
            .pathParam("userId", userId)
            .pathParam("productId", productId)
            .when()
            .get("/api/users/{userId}/products/{productId}")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testOrderCreation() {
        String userId = testDataHolder.get("userId", String.class);

        String orderBody = """
            {
                "userId": "%s",
                "items": [
                    {
                        "productId": "456",
                        "quantity": 2
                    }
                ]
            }
        """.formatted(userId);

        given(requestSpec)
            .body(orderBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }
}

 */

/*
@Suite
@SuiteDisplayName("API Test Suite")
@SelectClasses({
    EntityCreationTest.class,
    EntityDependentTest.class
})
@Order(1)
public class ApiTestSuite {
    // JUnit 5.8+ requires this empty class
}

 */

/*
Add to src/test/resources/junit-platform.properties:

Copy
junit.jupiter.testclass.order.default=org.junit.jupiter.api.ClassOrderer$ClassName
junit.jupiter.testmethod.order.default=org.junit.jupiter.api.MethodOrderer$OrderAnnotation
 */

/*
cleanup

@SpringBootTest
public class CleanupTest extends ApiTestBase {

    @Test
    @Order(Integer.MAX_VALUE) // Run last
    void cleanupTestData() {
        String userId = testDataHolder.get("userId", String.class);
        given(requestSpec)
            .delete("/api/users/{id}", userId)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        testDataHolder.clear();
    }
}

 */

/*
Alternative Storage Options:
For cross-module tests, use a file-based storage:
Copy
public class FileDataHolder {
    private static final Path STORAGE_FILE = Paths.get("target/test-data.json");

    public void store(String key, String value) throws IOException {
        Map<String, String> data = readFile();
        data.put(key, value);
        Files.write(STORAGE_FILE, Collections.singleton(JSON.toJSONString(data)));
    }

    public String get(String key) throws IOException {
        return readFile().get(key);
    }

    private Map<String, String> readFile() throws IOException {
        if (!Files.exists(STORAGE_FILE)) {
            return new HashMap<>();
        }
        String content = Files.readString(STORAGE_FILE);
        return JSON.parseObject(content, new TypeReference<Map<String, String>>() {});
    }
}
 */