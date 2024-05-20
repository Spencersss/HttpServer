package dev.spence.http;

import dev.spence.pojos.HttpMethod;
import dev.spence.pojos.HttpRequest;
import dev.spence.pojos.HttpRequestBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class HttpDecoder {

    // Converts and maps byte stream data of http request to HttpRequest object and relevant fields
    public static Optional<HttpRequest> decode(InputStream inputStream) throws IOException {
        // Convert input stream of bytes to buffered stream of characters to read line-by-line
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Retrieve http details including http method, uri, and http protocol used
        String rawRequest = reader.readLine();
        String[] httpDetails = rawRequest.split(" ");

        // Retrieve headers
        Map<String, String> headers = new HashMap<>();
        String currentHeader;
        while ((currentHeader = reader.readLine()) != null && !currentHeader.isEmpty()) {
            // Split header name and value on ':' and add to map of header name(s) -> values.
            String[] splitHeader = currentHeader.split(":");
            headers.put(splitHeader[0], splitHeader[1].trim());
        }

        // Retrieve body of request
        char[] bodyBuffer = new char[528];
        int charsRead = reader.read(bodyBuffer);
        HttpRequestBody body = new HttpRequestBody(String.valueOf(bodyBuffer));

        // Create request pojo and populate values
        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(httpDetails[0]);
        } catch (IllegalArgumentException e) {
            httpMethod = HttpMethod.UNKNOWN;
        }

        HttpRequest request = HttpRequest.builder()
                .method(httpMethod)
                .uri(httpDetails[1])
                .protocol(httpDetails[2])
                .headers(headers)
                .body(body)
                .build();

        return Optional.of(request);
    }

}
