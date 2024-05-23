package dev.spence.http;

import dev.spence.http.request.HttpPlainRequestBody;
import dev.spence.http.request.HttpRequest;
import dev.spence.http.request.HttpRequestBody;
import dev.spence.pojos.HttpContentType;
import dev.spence.pojos.HttpMethod;
import dev.spence.pojos.HttpTransferEncoding;
import dev.spence.server.Logging;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpDecoder {

    public static String CHUNK_ENCODING_DELIMITER = "\r\n";
    public static String CONTENT_LENGTH_HEADER_KEY = "content-length";
    public static String CONTENT_TYPE_HEADER_KEY = "content-type";
    public static String TRANSFER_ENCODING_HEADER_KEY = "transfer-encoding";

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
            headers.put(splitHeader[0].toLowerCase(), splitHeader[1].trim());
        }

        Optional<Integer> contentLength = getContentLengthHeader(headers);
        Optional<HttpContentType> contentType = getContentTypeHeader(headers);
        Optional<HttpTransferEncoding> transferEncoding = getTransferEncodingHeader(headers);

        // Retrieve body and http method for request
        HttpRequestBody body = readBody(reader, contentType.orElse(null), transferEncoding.orElse(null), contentLength.orElse(null));
        HttpMethod httpMethod = HttpMethod.getMethod(httpDetails[0]);

        // Create request pojo and populate values
        HttpRequest request = HttpRequest.builder()
                .method(httpMethod)
                .uri(httpDetails[1])
                .protocol(httpDetails[2])
                .headers(headers)
                .body(body)
                .build();

        return Optional.of(request);
    }

    /**
     * Reads and returns an HTTP request body from a provided reader and header values.
     */
    private static HttpRequestBody readBody(BufferedReader reader, HttpContentType contentType,
                                            HttpTransferEncoding transferEncoding, Integer contentLength) {
        HttpRequestBody requestBody = null;

        if (contentLength != null && contentType != null) {
            switch(contentType) {
                case TEXT_PLAIN:
                case APPLICATION_JSON:
                    requestBody = readPlainBody(reader, contentLength); break;
            }
        }

        // TODO: implement ability to read body without content length header present.

        return requestBody;
    }

    // Reads an HTTP request body of plain content type into an HttpPlainRequestBody.
    private static HttpPlainRequestBody readPlainBody(Reader reader, int bodyByteLength) {
        char[] bodyBuffer = new char[bodyByteLength];
        int totalCharsRead = 0;

        if (totalCharsRead < bodyByteLength) {
            try {
                while (totalCharsRead < bodyByteLength) {
                    int charsRead = reader.read(bodyBuffer);
                    if (charsRead == -1) break;
                    totalCharsRead += charsRead;
                }
            } catch (IOException e) {
                Logging.LOGGER.error(e.toString());
            }
        }

        return new HttpPlainRequestBody(Arrays.toString(bodyBuffer));
    }

    private static Optional<Integer> getContentLengthHeader(Map<String, String> headers) {
        Optional<String> rawHeaderValue = Optional.ofNullable(headers.get(CONTENT_LENGTH_HEADER_KEY));
        int headerValue = 0;
        if (rawHeaderValue.isPresent()) {
            try {
                headerValue = Integer.parseInt(rawHeaderValue.get());
            } catch (NumberFormatException e) {
                Logging.LOGGER.error(e.getMessage());
            }
        }

        return Optional.of(headerValue);
    }

    private static Optional<HttpContentType> getContentTypeHeader(Map<String, String> headers) {
        Optional<String> rawHeaderValue = Optional.ofNullable(headers.get(CONTENT_TYPE_HEADER_KEY));
        HttpContentType headerValue = null;
        if (rawHeaderValue.isPresent()) {
            try {
                headerValue = HttpContentType.getType(rawHeaderValue.get());
            } catch (IllegalArgumentException e) {
                Logging.LOGGER.error(e.getMessage());
            }
        }

        return Optional.ofNullable(headerValue);
    }

    private static Optional<HttpTransferEncoding> getTransferEncodingHeader(Map<String, String> headers) {
        Optional<String> rawHeaderValue = Optional.ofNullable(headers.get(TRANSFER_ENCODING_HEADER_KEY));
        HttpTransferEncoding headerValue = null;
        if (rawHeaderValue.isPresent()) {
            try {
                headerValue = HttpTransferEncoding.getEncoding(rawHeaderValue.get());
            } catch (IllegalArgumentException e) {
                Logging.LOGGER.error(e.getMessage());
            }
        }

        return Optional.ofNullable(headerValue);
    }

}
