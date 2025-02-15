package pl.where2play.restapie2etest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pl.where2play.restapie2etest.base.ApiTestBase;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchApiTest extends ApiTestBase {
    private static final String SEARCH_PATH = "/search";

    @Test
    void testAdvancedSearch() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", "test");
        queryParams.put("type", "DOCUMENT");
        queryParams.put("dateFrom", "2024-01-01");
        queryParams.put("dateTo", "2024-12-31");
        queryParams.put("tags", "important,urgent");

        Response response = executeRequest(
                "GET",
                SEARCH_PATH,
                null,
                queryParams,
                getAuthHeaders(),
                null,
                null
        );

        validateResponse(response)
                .validateStatusCode(HttpStatus.OK.value())
                .validateFieldExists("results")
                .validateArrayMinSize("results", 1)
                .validateCustom(r -> {
                    JsonPath jsonPath = r.jsonPath();

                    // Validate search results
                    List<Map<String, Object>> results = jsonPath.getList("results");
                    assertThat(results)
                            .allMatch(result ->
                                    result.containsKey("id") &&
                                            result.containsKey("type") &&
                                            result.containsKey("score")
                            );

                    // Validate relevance scores
                    List<Double> scores = jsonPath.getList("results.score");
                    assertThat(scores)
                            .allMatch(score -> score >= 0.0 && score <= 1.0)
                            .isSortedAccordingTo(Comparator.reverseOrder());

                    // Validate highlighting
                    Map<String, List<String>> highlights =
                            jsonPath.getMap("results[0].highlights");
                    assertThat(highlights).isNotEmpty();
                });
    }
}

