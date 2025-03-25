package pl.where2play.restapie2etest;

public class Validation20250325 {
//    ok now please include all possible validation which can occur while testing controllers


//    MonicaGPT-4o
//    To create a comprehensive validation framework for testing controllers, you should account for all possible scenarios that might occur in a typical validation process. Below is a list of all possible validations that you might need, followed by an updated implementation of the validateField method to handle them.

//    Comprehensive Validation Scenarios
//* Existence Validation:
//            * *exists*: Field must exist and not be null.
//            * *no-exists*: Field must not exist (or be null).
//            * Null Validation:
//            * *null*: Field must explicitly be null.
//            * *not-null*: Field must not be null.
//            * Type Validation:
//            * *number*: Field must be a numeric type.
//            * *string*: Field must be a string.
//    * *boolean*: Field must be a boolean.
//            * *array*: Field must be an array.
//    * *object*: Field must be an object.
//* Regex Validation:
//            * regex:<pattern>: Field must match the given regular expression.
//* Range Validation:
//            * *min:<value>*: Field value must be greater than or equal to <value>.
//            * *max:<value>*: Field value must be less than or equal to <value>.
//            * Size Validation:
//            * For arrays or collections:
//            * size:<value>: Array or collection must have exactly <value> elements.
//        * min-size:<value>: Array or collection must have at least <value> elements.
//        * max-size:<value>: Array or collection must have no more than <value> elements.
//* Equality Validation:
//            * Exact match with a value (e.g., 123, "test", etc.).
//            * Custom Validations:
//            * Allow custom validation logic using lambdas or functional interfaces.
//
//            Updated validateField Implementation
//    Here’s the updated validateField method to include all the above scenarios:
//    java

    private void validateField(String path, Object actual, Object expected) {
        if (expected instanceof String) {
            String expectedStr = (String) expected;

            // 1. Existence Validation
            if (expectedStr.equals("*exists*")) {
                assertThat(actual).isNotNull();
            } else if (expectedStr.equals("*no-exists*")) {
                assertThat(actual).isNull();

                // 2. Null Validation
            } else if (expectedStr.equals("*null*")) {
                assertThat(actual).isNull();
            } else if (expectedStr.equals("*not-null*")) {
                assertThat(actual).isNotNull();

                // 3. Type Validation
            } else if (expectedStr.equals("*number*")) {
                assertThat(actual).isInstanceOf(Number.class);
            } else if (expectedStr.equals("*string*")) {
                assertThat(actual).isInstanceOf(String.class);
            } else if (expectedStr.equals("*boolean*")) {
                assertThat(actual).isInstanceOf(Boolean.class);
            } else if (expectedStr.equals("*array*")) {
                assertThat(actual).isInstanceOf(List.class);
            } else if (expectedStr.equals("*object*")) {
                assertThat(actual).isInstanceOf(Map.class);

                // 4. Regex Validation
            } else if (expectedStr.startsWith("regex:")) {
                String pattern = expectedStr.substring(6);
                assertThat(actual.toString()).matches(pattern);

                // 5. Range Validation
            } else if (expectedStr.startsWith("*min:")) {
                double min = Double.parseDouble(expectedStr.substring(5));
                assertThat(((Number) actual).doubleValue()).isGreaterThanOrEqualTo(min);
            } else if (expectedStr.startsWith("*max:")) {
                double max = Double.parseDouble(expectedStr.substring(5));
                assertThat(((Number) actual).doubleValue()).isLessThanOrEqualTo(max);

                // 6. Size Validation (for arrays or collections)
            } else if (expectedStr.startsWith("size:")) {
                int size = Integer.parseInt(expectedStr.substring(5));
                assertThat(((List<?>) actual)).hasSize(size);
            } else if (expectedStr.startsWith("min-size:")) {
                int minSize = Integer.parseInt(expectedStr.substring(9));
                assertThat(((List<?>) actual)).hasSizeGreaterThanOrEqualTo(minSize);
            } else if (expectedStr.startsWith("max-size:")) {
                int maxSize = Integer.parseInt(expectedStr.substring(9));
                assertThat(((List<?>) actual)).hasSizeLessThanOrEqualTo(maxSize);

                // 7. Equality Validation (default case)
            } else {
                assertThat(actual).isEqualTo(expected);
            }

        } else if (expected instanceof List) {
            // Validate lists
            assertThat(actual).isInstanceOf(List.class);
            List<?> actualList = (List<?>) actual;
            List<?> expectedList = (List<?>) expected;

            assertThat(actualList).containsExactlyElementsOf(expectedList);

        } else if (expected instanceof Map) {
            // Validate nested objects
            assertThat(actual).isInstanceOf(Map.class);
            Map<String, Object> actualMap = (Map<String, Object>) actual;
            Map<String, Object> expectedMap = (Map<String, Object>) expected;

            expectedMap.forEach((key, value) -> {
                validateField(path + "." + key, actualMap.get(key), value);
            });

        } else if (expected instanceof Predicate) {
            // 8. Custom Validation
            Predicate<Object> predicate = (Predicate<Object>) expected;
            assertThat(predicate.test(actual)).isTrue();

        } else {
            // Default: Validate equality
            assertThat(actual).isEqualTo(expected);
        }
    }

//    Example Test Cases
//    Example 1: Validate All Scenarios
//    Sample JSON Response:
//    json
//
//            Copy
//    {
//        "id": 123,
//            "name": "test",
//            "active": true,
//            "details": {
//        "description": "This is a test",
//                "priority": 5
//    },
//        "tags": ["tag1", "tag2"],
//        "attachments": []
//    }
//    Validation:
//    java
//
//            Copy
    @Test
    void testValidateAllScenarios() throws Exception {
        Response response = executeRequest("GET", "/api/resource", null, null);

        validateResponse(response, 200, Map.of(
                "id", "*number*",
                "name", regex:"[a-z]+",  // Regex validation
                "active", "*boolean*",
                "details", Map.of(
                "description", "*string*",
                "priority", "*min:1*"
        ),
                "tags", Map.of(
                "size", 2, // Size validation
                "each", "*string*"
        ),
                "attachments", "*array*"   // empty JSON

//        pls share samples for  8 case: custom or lambda


//
//
//
//
//￼
//        MonicaSonnet
//        Custom Validation with Lambda Functions
//        Lambda functions and custom predicates offer powerful flexibility for complex validations that can't be expressed with simple string patterns. Here are comprehensive examples for implementing custom validations in your controller testing framework.
//        Updated validateField Method with Lambda Support
//        First, let's ensure the validateField method properly supports lambda expressions:
//        java
//
//                Copy
        private void validateField(String path, Object actual, Object expected) {
            // ... existing code ...

        } else if (expected instanceof Predicate) {
            // 8. Custom Validation with Lambda/Predicate
            Predicate<Object> predicate = (Predicate<Object>) expected;
            boolean result = predicate.test(actual);
            assertThat(result)
                    .as("Custom validation failed for field at path '%s'", path)
                    .isTrue();

            // ... rest of the method ...
        }
//        Example Test Cases for Custom Validation
//        Example 1: Basic Lambda Validation
//                java
//
//        Copy
        @Test
        void testCustomValidationWithLambda() throws Exception {
            Response response = executeRequest("GET", "/api/users/123", null, null);

            validateResponse(response, 200, Map.of(
                    "id", 123,
                    "name", "John Doe",
                    "email", (Predicate<Object>) email ->
                            email != null && email.toString().contains("@") && email.toString().endsWith(".com"),
                    "createdAt", (Predicate<Object>) timestamp -> {
                        if (!(timestamp instanceof String)) return false;
                        try {
                            LocalDateTime dateTime = LocalDateTime.parse((String) timestamp);
                            return dateTime.isBefore(LocalDateTime.now());
                        } catch (Exception e) {
                            return false;
                        }
                    }
            ));
        }
//        Example 2: Complex Object Validation
//                java
//
//        Copy
        @Test
        void testComplexObjectValidation() throws Exception {
            Response response = executeRequest("GET", "/api/orders/456", null, null);

            validateResponse(response, 200, Map.of(
                    "orderId", 456,
                    "items", (Predicate<Object>) items -> {
                        if (!(items instanceof List)) return false;
                        List<?> itemsList = (List<?>) items;

                        // Check if list is not empty
                        if (itemsList.isEmpty()) return false;

                        // Check if all items have a price > 0
                        return itemsList.stream()
                                .allMatch(item -> {
                                    if (!(item instanceof Map)) return false;
                                    Map<?, ?> itemMap = (Map<?, ?>) item;
                                    Object price = itemMap.get("price");
                                    return price instanceof Number && ((Number) price).doubleValue() > 0;
                                });
                    },
                    "totalAmount", (Predicate<Object>) total -> {
                        if (!(total instanceof Number)) return false;
                        double totalAmount = ((Number) total).doubleValue();

                        // Get the items list to calculate expected total
                        List<?> items = (List<?>) ((Map<?, ?>) response.jsonPath().get("")).get("items");
                        double expectedTotal = items.stream()
                                .mapToDouble(item -> {
                                    Map<?, ?> itemMap = (Map<?, ?>) item;
                                    Number price = (Number) itemMap.get("price");
                                    Number quantity = (Number) itemMap.get("quantity");
                                    return price.doubleValue() * quantity.doubleValue();
                                })
                                .sum();

                        // Check if total matches calculated sum (allowing small floating point difference)
                        return Math.abs(totalAmount - expectedTotal) < 0.001;
                    }
            ));
        }
//        Example 3: Reusable Custom Validators
//        Create reusable validators for common validation patterns:
//        java
//
//                Copy
        public class CustomValidators {
            // Email validator
            public static Predicate<Object> isValidEmail() {
                return obj -> obj instanceof String &&
                        ((String) obj).matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            }

            // UUID validator
            public static Predicate<Object> isValidUuid() {
                return obj -> obj instanceof String &&
                        ((String) obj).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
            }

            // Date validator (ISO format and before current time)
            public static Predicate<Object> isValidPastDate() {
                return obj -> {
                    if (!(obj instanceof String)) return false;
                    try {
                        LocalDateTime date = LocalDateTime.parse((String) obj);
                        return date.isBefore(LocalDateTime.now());
                    } catch (Exception e) {
                        return false;
                    }
                };
            }

            // Numeric range validator
            public static Predicate<Object> isInRange(double min, double max) {
                return obj -> {
                    if (!(obj instanceof Number)) return false;
                    double value = ((Number) obj).doubleValue();
                    return value >= min && value <= max;
                };
            }

            // Collection size validator
            public static Predicate<Object> hasItemCount(int expectedSize) {
                return obj -> {
                    if (!(obj instanceof Collection)) return false;
                    return ((Collection<?>) obj).size() == expectedSize;
                };
            }
        }
//        Example 4: Using Reusable Validators
//                java
//
//        Copy
        @Test
        void testWithReusableValidators() throws Exception {
            Response response = executeRequest("GET", "/api/customers", null, null);

            validateResponse(response, 200, Map.of(
                    "$array", Map.of(
                            "each", Map.of(
                                    "id", CustomValidators.isValidUuid(),
                                    "email", CustomValidators.isValidEmail(),
                                    "registrationDate", CustomValidators.isValidPastDate(),
                                    "loyaltyPoints", CustomValidators.isInRange(0, 1000),
                                    "orders", CustomValidators.hasItemCount(5)
                            )
                    )
            ));
        }
//        Example 5: Combining Multiple Validations
//                java
//
//        Copy
        @Test
        void testCombinedValidations() throws Exception {
            Response response = executeRequest("GET", "/api/products/789", null, null);

            validateResponse(response, 200, Map.of(
                    "product", Map.of(
                            "id", 789,
                            "attributes", (Predicate<Object>) attributes -> {
                                if (!(attributes instanceof Map)) return false;
                                Map<?, ?> attrMap = (Map<?, ?>) attributes;

                                // Check required keys exist
                                if (!attrMap.containsKey("color") || !attrMap.containsKey("size")) {
                                    return false;
                                }

                                // Validate color is from allowed list
                                String color = (String) attrMap.get("color");
                                List<String> allowedColors = List.of("red", "blue", "green", "black");
                                if (!allowedColors.contains(color)) {
                                    return false;
                                }

                                // Validate size format (S, M, L, XL)
                                String size = (String) attrMap.get("size");
                                return size.matches("^[SML]|XL$");
                            }
                    ),
                    "pricing", Map.of(
                            "basePrice", "*number*",
                            "discount", (Predicate<Object>) discount -> {
                                if (!(discount instanceof Number)) return false;
                                double discountValue = ((Number) discount).doubleValue();

                                // Discount must be between 0 and 100
                                return discountValue >= 0 && discountValue <= 100;
                            },
                            "finalPrice", (Predicate<Object>) (finalPrice) -> {
                                if (!(finalPrice instanceof Number)) return false;

                                // Get the base price and discount to calculate expected final price
                                Number basePrice = (Number) ((Map<?, ?>) response.jsonPath().get("pricing")).get("basePrice");
                                Number discount = (Number) ((Map<?, ?>) response.jsonPath().get("pricing")).get("discount");

                                double expectedFinalPrice = basePrice.doubleValue() * (1 - discount.doubleValue() / 100);
                                double actualFinalPrice = ((Number) finalPrice).doubleValue();

                                // Allow small floating point difference
                                return Math.abs(actualFinalPrice - expectedFinalPrice) < 0.01;
                            }
                    )
            ));
        }
//        Example 6: Handling Conditional Validations
//                java
//
//        Copy
        @Test
        void testConditionalValidations() throws Exception {
            Response response = executeRequest("GET", "/api/payments/123", null, null);

            validateResponse(response, 200, Map.of(
                    "paymentId", 123,
                    "status", "*string*",
                    "details", (Predicate<Object>) details -> {
                        if (!(details instanceof Map)) return false;
                        Map<?, ?> detailsMap = (Map<?, ?>) details;

                        // Get the parent object to check payment status
                        Map<?, ?> parent = response.jsonPath().getMap("");
                        String status = (String) parent.get("status");

                        if ("completed".equals(status)) {
                            // For completed payments, we expect confirmation code and timestamp
                            return detailsMap.containsKey("confirmationCode") &&
                                    detailsMap.containsKey("completedAt");
                        } else if ("failed".equals(status)) {
                            // For failed payments, we expect error code and message
                            return detailsMap.containsKey("errorCode") &&
                                    detailsMap.containsKey("errorMessage");
                        } else if ("pending".equals(status)) {
                            // For pending payments, we expect initiated timestamp
                            return detailsMap.containsKey("initiatedAt");
                        }

                        return false;
                    }
            ));
        }
//        Example 7: Validating JSON Schema Structure
//        java
//
//                Copy
        @Test
        void testJsonSchemaValidation() throws Exception {
            Response response = executeRequest("GET", "/api/documents/123", null, null);

            // Create a custom validator that checks the entire structure
            Predicate<Object> documentSchemaValidator = document -> {
                if (!(document instanceof Map)) return false;
                Map<?, ?> docMap = (Map<?, ?>) document;

                // Check required fields
                List<String> requiredFields = List.of("id", "title", "content", "metadata", "permissions");
                for (String field : requiredFields) {
                    if (!docMap.containsKey(field)) return false;
                }

                // Check metadata structure
                Object metadata = docMap.get("metadata");
                if (!(metadata instanceof Map)) return false;
                Map<?, ?> metaMap = (Map<?, ?>) metadata;
                if (!metaMap.containsKey("createdBy") || !metaMap.containsKey("createdAt")) {
                    return false;
                }

                // Check permissions structure
                Object permissions = docMap.get("permissions");
                if (!(permissions instanceof List)) return false;
                List<?> permList = (List<?>) permissions;

                return permList.stream().allMatch(perm -> {
                    if (!(perm instanceof Map)) return false;
                    Map<?, ?> permMap = (Map<?, ?>) perm;
                    return permMap.containsKey("userId") && permMap.containsKey("level");
                });
            };

            validateResponse(response, 200, Map.of(
                    "document", documentSchemaValidator
            ));
        }
//        Example 8: Type-Safe Custom Validators
//        java
//
//                Copy
// Define type-safe validators with generics
        public interface TypedValidator<T> {
            boolean validate(T value);

            // Convert to Predicate<Object> for use with validateField
            default Predicate<Object> toPredicate() {
                return obj -> {
                    try {
                        @SuppressWarnings("unchecked")
                        T typedValue = (T) obj;
                        return validate(typedValue);
                    } catch (ClassCastException e) {
                        return false;
                    }
                };
            }
        }

// Example implementations
        public class StringValidators {
            public static TypedValidator<String> matchesPattern(String pattern) {
                return value -> value != null && value.matches(pattern);
            }

            public static TypedValidator<String> hasLength(int min, int max) {
                return value -> value != null && value.length() >= min && value.length() <= max;
            }
        }

        public class NumberValidators {
            public static TypedValidator<Number> isPositive() {
                return value -> value != null && value.doubleValue() > 0;
            }

            public static TypedValidator<Number> isInRange(double min, double max) {
                return value -> value != null &&
                        value.doubleValue() >= min &&
                        value.doubleValue() <= max;
            }
        }

// Usage in tests
        @Test
        void testWithTypedValidators() throws Exception {
            Response response = executeRequest("GET", "/api/products", null, null);

            validateResponse(response, 200, Map.of(
                    "name", StringValidators.hasLength(3, 50).toPredicate(),
                    "sku", StringValidators.matchesPattern("^[A-Z]{2}-\\d{4}$").toPredicate(),
                    "price", NumberValidators.isPositive().toPredicate(),
                    "rating", NumberValidators.isInRange(1, 5).toPredicate()
            ));
        }
        Integrating Custom Validation into validateResponse
        To fully integrate custom validation, you might want to update your main validateResponse method:
        java

                Copy
        public void validateResponse(Response response, int expectedStatus, Object expectedBody) {
            // Validate status code
            assertThat(response.getStatusCode()).isEqualTo(expectedStatus);

            // Skip body validation if expectedBody is null
            if (expectedBody == null) {
                return;
            }

            // Handle special case for array validation
            if (expectedBody instanceof Map && ((Map<?, ?>) expectedBody).containsKey("$array")) {
                validateArrayResponse(response, (Map<String, Object>) ((Map<?, ?>) expectedBody).get("$array"));
                return;
            }

            // For regular object validation
            Map<String, Object> responseMap = response.jsonPath().getMap("");
            if (expectedBody instanceof Map) {
                Map<String, Object> expectedMap = (Map<String, Object>) expectedBody;
                expectedMap.forEach((key, value) -> {
                    validateField(key, responseMap.get(key), value);
                });
            } else {
                // Direct validation (e.g., for primitive types or custom validators)
                validateField("$", responseMap, expectedBody);
            }
        }
//        These examples showcase the power and flexibility of custom validations using lambda expressions and predicates in your testing framework. They allow you to create complex, reusable validation logic that goes beyond simple equality or pattern matching.

    }
