package pl.where2play.restapie2etest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductApiTest extends ApiTestBase {
    private static final String PRODUCTS_PATH = "/products";

    @Test
    void testCreateProductWithVariants() {
        String productPayload = """
            {
                "name": "Premium Laptop",
                "category": "ELECTRONICS",
                "price": 999.99,
                "variants": [
                    {
                        "sku": "LAP-8GB-256",
                        "specs": {
                            "ram": "8GB",
                            "storage": "256GB"
                        }
                    },
                    {
                        "sku": "LAP-16GB-512",
                        "specs": {
                            "ram": "16GB",
                            "storage": "512GB"
                        }
                    }
                ]
            }
            """;

        Response response = executeRequest(
                "POST",
                PRODUCTS_PATH,
                productPayload,
                null,
                getAuthHeaders(),
                null,
                null
        );

        validateResponse(response)
                .validateStatusCode(HttpStatus.CREATED.value())
                .validateFieldExists("id")
                .validateField("name", "Premium Laptop")
                .validateArraySize("variants", 2)
                .validateCustom(r -> {
                    // Complex validation of nested objects
                    JsonPath jsonPath = r.jsonPath();
                    List<Map<String, Object>> variants = jsonPath.getList("variants");

                    assertThat(variants).hasSize(2)
                            .allMatch(variant ->
                                    variant.containsKey("sku") &&
                                            ((Map<String, Object>)variant.get("specs")).containsKey("ram")
                            );

                    // Validate SKU format
                    List<String> skus = jsonPath.getList("variants.sku");
                    assertThat(skus).allMatch(sku -> sku.startsWith("LAP-"));
                });
    }
}

