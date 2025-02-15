package pl.where2play.restapie2etest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FileApiTest extends ApiTestBase {
    private static final String UPLOAD_PATH = "/files/upload";

    @Test
    void testFileUploadWithMetadata() {
        File testFile = new File("src/test/resources/test-document.pdf");
        String metadata = """
            {
                "documentType": "CONTRACT",
                "tags": ["important", "legal"],
                "expiryDate": "2025-12-31"
            }
            """;

        Map<String, Object> multipartParams = new HashMap<>();
        multipartParams.put("file", testFile);
        multipartParams.put("metadata", metadata);

        Response response = executeRequest(
                "POST",
                UPLOAD_PATH,
                null,
                null,
                getAuthHeaders(),
                null,
                multipartParams
        );

        validateResponse(response)
                .validateStatusCode(HttpStatus.CREATED.value())
                .validateFieldExists("fileId")
                .validateFieldExists("downloadUrl")
                .validateField("mimeType", "application/pdf")
                .validateList("tags", Arrays.asList("important", "legal"))
                .validateCustom(r -> {
                    // Validate file-specific fields
                    JsonPath jsonPath = r.jsonPath();
                    assertThat(jsonPath.getLong("fileSize")).isGreaterThan(0);
                    assertThat(jsonPath.getString("downloadUrl"))
                            .startsWith("/api/v1/files/download/");

                    // Validate metadata
                    assertThat(jsonPath.getString("metadata.documentType"))
                            .isEqualTo("CONTRACT");
                    assertThat(jsonPath.getString("metadata.expiryDate"))
                            .matches("\\d{4}-\\d{2}-\\d{2}");
                });
    }
}

