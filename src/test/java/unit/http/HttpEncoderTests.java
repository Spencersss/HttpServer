package unit.http;

import dev.spence.http.HttpEncoder;
import dev.spence.http.response.HttpPlainResponseBody;
import dev.spence.http.response.HttpResponse;
import dev.spence.http.response.HttpResponseBody;
import dev.spence.util.ByteUtil;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpEncoderTests {

    @Test
    public void encodeValidHttpResponse() {
        // Given
        String expectedResponseString = "HTTP/1.1 200 Success\r\n" +
                "testt: test321\r\n" +
                "content-length: 9\r\n" +
                "host: test\r\n" +
                "\r\n" +
                "test body";
        int bodyLength = expectedResponseString.getBytes(StandardCharsets.UTF_8).length;

        Map<String, String> headers = new HashMap<>();
        headers.put("host", "test");
        headers.put("testt","test321");

        HttpResponseBody body = HttpPlainResponseBody.builder().contents("test body").build();

        HttpResponse response = HttpResponse.builder()
                .protocol("HTTP/1.1")
                .statusCode(200)
                .statusText("Success")
                .headers(headers)
                .body(body)
                .build();

        // When
        List<Byte> responseBytes = HttpEncoder.encode(response);

        // Then
        assertNotNull(responseBytes);
        assertEquals(bodyLength, responseBytes.size());

        String responseString = ByteUtil.getStringFromByteList(responseBytes);
        assertEquals(expectedResponseString, responseString);
    }

}
