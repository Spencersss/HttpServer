package unit.http;

import dev.spence.http.HttpDecoder;
import dev.spence.pojos.HttpMethod;
import dev.spence.http.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpDecoderTests {

    @Test
    public void decodeValidHttpRequest() throws IOException {
        // Given
        String validRequest = "GET /test HTTP/1.1\n" +
                "Host: localhost:80\n" +
                "User-Agent: curl/8.2.1\n" +
                "Accept: */*";
        byte[] requestBytes = validRequest.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(requestBytes);

        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("host", "localhost");
        expectedHeaders.put("user-agent", "curl/8.2.1");
        expectedHeaders.put("accept", "*/*");

        // When
        Optional<HttpRequest> requestOptional = HttpDecoder.decode(inputStream);

        // Then
        assertNotNull(requestOptional);
        assertTrue(requestOptional.isPresent());

        HttpRequest resultingRequest = requestOptional.get();
        assertEquals(HttpMethod.GET, resultingRequest.getMethod());
        assertEquals("/test", resultingRequest.getUri());
        assertEquals("HTTP/1.1", resultingRequest.getProtocol());

        assertEquals(3, resultingRequest.getHeaders().size());
        assertEquals(expectedHeaders, resultingRequest.getHeaders());
    }

}
