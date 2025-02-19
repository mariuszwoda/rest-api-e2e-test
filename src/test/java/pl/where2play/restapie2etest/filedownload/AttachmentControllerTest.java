package pl.where2play.restapie2etest.filedownload;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static pl.where2play.restapie2etest.base.ApiTestBase.getAuthHeaders;

public class AttachmentControllerTest extends ApiTestBase {
    private static final String ATTACHMENTS_PATH = "/atach";

    private static Stream<Arguments> provideAttachmentTestCases() {
        return Stream.of(
                // File download test case
                Arguments.of(
                        "GET",
                        ATTACHMENTS_PATH + "/{attId}",
                        null,  // no body
                        null,  // no query params
                        getAuthHeaders(),
                        new HashMap<String, Object>() {{
                            put("attId", 12345L);
                        }},
                        null,  // no multipart
                        HttpStatus.OK.value()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideAttachmentTestCases")
    void testAttachmentDownload(String method, String path, String body,
                                Map<String, Object> queryParams,
                                Map<String, String> headers,
                                Map<String, Object> pathParams,
                                Map<String, Object> multipartParams,
                                int expectedStatus) {
        Response response = executeRequest(method, path, body, queryParams,
                headers, pathParams, multipartParams);

        // Basic validation
        response.then()
                .statusCode(expectedStatus)
                .contentType(ContentType.BINARY);

        // Save the file to temporary location
        File downloadedFile = response.asFile();

        // Advanced validations
        assertThat(downloadedFile)
                .exists()
                .isFile()
                .isNotEmpty();

        // Clean up temporary file
        downloadedFile.delete();
    }

    // Add this to ApiTestBase to handle file responses
    protected File downloadFile(Response response) {
        try {
            File tempFile = File.createTempFile("download-", ".tmp");
            response.then().extract().response().body().asInputStream().transferTo(
                    Files.newOutputStream(tempFile.toPath())
            );
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }

    /*
    enhanced
     */
    @ParameterizedTest
    @MethodSource("provideAttachmentTestCases")
    void testAttachmentDownload(String method, String path, String body,
                                Map<String, Object> queryParams,
                                Map<String, String> headers,
                                Map<String, Object> pathParams,
                                Map<String, Object> multipartParams,
                                int expectedStatus) {
        Response response = executeRequest(method, path, body, queryParams,
                headers, pathParams, multipartParams);

        // Basic validation
        response.then()
                .statusCode(expectedStatus)
                .contentType(ContentType.BINARY);

        // Validate headers
        validateFileHeaders(response, "example.pdf");

        // Save and validate the file
        File downloadedFile = downloadFile(response);
        try {
            assertThat(downloadedFile)
                    .exists()
                    .isFile()
                    .isNotEmpty()
                    .hasExtension("pdf")
                    .hasSizeGreaterThan(100);  // Minimum expected file size

            // Add content validation if needed
            assertThat(FileUtils.readFileToByteArray(downloadedFile))
                    .isNotEmpty()
                    .startsWith("%PDF-".getBytes());  // PDF magic number

        } finally {
            downloadedFile.delete();
        }
    }

    /*
    test diff scenarios
     */

//    private static Stream<Arguments> provideAttachmentTestCases() {
//        return Stream.of(
//                // Valid attachment
//                Arguments.of(..., 12345L, HttpStatus.OK.value()),
//
//        // Non-existent attachment
//        Arguments.of(..., 99999L, HttpStatus.NOT_FOUND.value()),
//
//        // Unauthorized access
//        Arguments.of(..., 12345L, HttpStatus.FORBIDDEN.value())
//    );
//    }

    /*
    fix for asFile
     */

    @ParameterizedTest
    @MethodSource("provideAttachmentTestCases")
    void testAttachmentDownload2(String method, String path, String body,
                                Map<String, Object> queryParams,
                                Map<String, String> headers,
                                Map<String, Object> pathParams,
                                Map<String, Object> multipartParams,
                                int expectedStatus) {
        Response response = executeRequest(method, path, body, queryParams,
                headers, pathParams, multipartParams);

        // Basic validation
        response.then()
                .statusCode(expectedStatus)
                .contentType(ContentType.BINARY);

        // Validate headers
        validateFileHeaders(response, "expected-filename.pdf");

        // Get file content and validate
        byte[] fileContent = response.asByteArray();
        assertThat(fileContent)
                .isNotEmpty()
                .hasSizeGreaterThan(100);  // Validate minimum file size

        // Optional: Validate file signature/magic number
        assertThat(Arrays.copyOfRange(fileContent, 0, 4))
                .isEqualTo(new byte[] {0x25, 0x50, 0x44, 0x46}); // PDF magic number

        // Optional: Save to temporary file for further validation
        File tempFile = null;
        try {
            tempFile = File.createTempFile("download-", ".tmp");
            Files.write(tempFile.toPath(), fileContent);

            // Additional file validations
            assertThat(tempFile)
                    .exists()
                    .isFile()
                    .hasExtension("pdf")
                    .hasSize(fileContent.length);

        } catch (IOException e) {
            fail("Failed to create temp file", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /*
    alternative approach
     */

    // In test method
//try (InputStream stream = response.getBody().asInputStream()) {
//        byte[] fileContent = IOUtils.toByteArray(stream);
//        // Perform validations on fileContent
//    } catch (IOException e) {
//        fail("Failed to read response stream", e);
//    }


//    response.then().contentType("application/pdf");
//    response.then().header("Content-Length", String.valueOf(expectedSize));
//    response.then()
//            .header("Content-Disposition", matchesPattern("attachment; filename=\".+\\.pdf\""));
//
//    String expectedChecksum = "d41d8cd98f00b204e9800998ecf8427e"; // MD5 example
//    String actualChecksum = DigestUtils.md5Hex(fileContent);
//    assertThat(actualChecksum).isEqualTo(expectedChecksum);



}
