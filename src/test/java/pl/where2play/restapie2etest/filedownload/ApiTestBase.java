package pl.where2play.restapie2etest.filedownload;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

//import static java.util.function.Predicate.not;
import static org.hamcrest.Matchers.*;


public abstract class ApiTestBase {
    // ... existing code ...

    protected Response executeRequest(String method, String path, String body,
                                      Map<String, Object> queryParams,
                                      Map<String, String> headers,
                                      Map<String, Object> pathParams,
                                      Map<String, Object> multipartParams) {
        RequestSpecification requestSpec = null; //todo
        RequestSpecification request = RestAssured.given(requestSpec)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .appendDefaultContentCharsetToContentTypeIfUndefined(false)));

        // ... rest of the executeRequest method ...
        return null;
    }

    protected void validateFileHeaders2(Response response, String expectedFilename) {
        response.then()
                .header("Content-Disposition", containsString("attachment"))
                .header("Content-Disposition", containsString("filename=\"" + expectedFilename + "\""));
    }

    /*
    second fix
     */

    protected byte[] downloadFileContent(Response response) {
        return response.getBody().asByteArray();
    }

    protected void validateFileHeaders3(Response response, String expectedFilename) {
        response.then()
                .header("Content-Disposition", containsString("attachment"))
                .header("Content-Disposition", containsString("filename=\"" + expectedFilename + "\""))
                .header("Content-Type", not(emptyOrNullString()));
    }

}
