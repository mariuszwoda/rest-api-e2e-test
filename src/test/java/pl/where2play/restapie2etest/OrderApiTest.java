package pl.where2play.restapie2etest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderApiTest extends ApiTestBase {
    private static final String ORDERS_PATH = "/orders";

    @Test
    void testCreateAndUpdateOrder() {
        // Create order
        String orderPayload = """
            {
                "customerId": "CUST123",
                "items": [
                    {
                        "productId": "PROD1",
                        "quantity": 2,
                        "price": 29.99
                    }
                ],
                "shippingAddress": {
                    "street": "123 Main St",
                    "city": "Boston",
                    "zipCode": "02108"
                }
            }
            """;

        Response createResponse = executeRequest(
                "POST",
                ORDERS_PATH,
                orderPayload,
                null,
                getAuthHeaders(),
                null,
                null
        );

        String orderId = validateResponse(createResponse)
                .validateStatusCode(HttpStatus.CREATED.value())
                .validateFieldExists("id")
                .validateField("status", "PENDING")
                .validateField("totalAmount", 59.98)  // 2 * 29.99
                .validateCustom(r -> {
                    // Validate order number format
                    String orderNumber = r.jsonPath().getString("orderNumber");
                    assertThat(orderNumber).matches("ORD-\\d{8}-\\d{4}");
                })
                .extractField("id", String.class);

        // Update order status
        String updatePayload = """
            {
                "status": "CONFIRMED",
                "paymentDetails": {
                    "method": "CREDIT_CARD",
                    "transactionId": "TXN123"
                }
            }
            """;

        Response updateResponse = executeRequest(
                "PATCH",
                ORDERS_PATH + "/" + orderId,
                updatePayload,
                null,
                getAuthHeaders(),
                null,
                null
        );

        validateResponse(updateResponse)
                .validateStatusCode(HttpStatus.OK.value())
                .validateField("status", "CONFIRMED")
                .validateFieldExists("lastModifiedAt")
                .validateCustom(r -> {
                    // Validate audit fields
                    JsonPath jsonPath = r.jsonPath();
                    String createdAt = jsonPath.getString("createdAt");
                    String modifiedAt = jsonPath.getString("lastModifiedAt");
                    assertThat(modifiedAt).isGreaterThan(createdAt);
                });
    }
}

