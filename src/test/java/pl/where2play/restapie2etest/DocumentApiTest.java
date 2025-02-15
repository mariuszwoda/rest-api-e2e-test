package pl.where2play.restapie2etest;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import java.io.File;
import java.util.HashMap;
import java.util.stream.Stream;

import static io.restassured.RestAssured.put;

public class DocumentApiTest extends ApiTestBase {
    private static final String DOCUMENTS_PATH = "/documents";

    private Stream<Arguments> provideDocumentTestCases() {
        return Stream.of(
                // Upload document with metadata
                Arguments.of(
                        "POST",
                        DOCUMENTS_PATH + "/upload",
                        null,
                        null,
                        getAuthHeaders(),
                        null,
                        new HashMap<String, Object>() {{
                            put("file", new File("src/test/resources/test-doc.pdf"));
                            put("metadata", """
                        {
                            "documentType": "CONTRACT",
                            "department": "LEGAL",
                            "tags": ["confidential", "2024"]
                        }
                    """);
                        }},
                        HttpStatus.CREATED.value()
                ),

                // Get document with version history
                Arguments.of(
                        "GET",
                        DOCUMENTS_PATH + "/{id}/versions",
                        null,
                        new HashMap<String, Object>() {{
                            put("includeDeleted", false);
                            put("from", "2024-01-01");
                        }},
                        getAuthHeaders(),
                        new HashMap<String, Object>() {{
                            put("id", "doc123");
                        }},
                        null,
                        HttpStatus.OK.value()
                ),

                // Bulk tag documents
                Arguments.of(
                        "POST",
                        DOCUMENTS_PATH + "/bulk/tags",
                        """
                        {
                            "documentIds": ["doc123", "doc456"],
                            "tagsToAdd": ["urgent", "review"],
                            "tagsToRemove": ["draft"]
                        }
                        """,
                        null,
                        getAuthHeaders(),
                        null,
                        null,
                        HttpStatus.OK.value()
                )
        );
    }
}
