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
import java.util.function.Function;

public class HttpDecoder {

    public static String CONTENT_LENGTH_HEADER_KEY = "content-length";
    public static String CONTENT_TYPE_HEADER_KEY = "content-type";
    public static String TRANSFER_ENCODING_HEADER_KEY = "transfer-encoding";

    // Converts and maps byte stream data of http request to HttpRequest object and relevant fields
    public static Optional<HttpRequest> decode(InputStream inputStream) throws IOException {
        // Convert input stream of bytes to buffered stream of characters to read line-by-line
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Retrieve http details including http method, uri, and http protocol used
        Optional<String> rawRequest = Optional.ofNullable(reader.readLine());

        if (rawRequest.isPresent()) {
            String[] httpDetails = rawRequest.get().split(" ");

            // Retrieve query parameters if present
            Map<String, String> queryParams = getQueryParamsFromURI(httpDetails[1]);

            // Retrieve headers
            Map<String, String> headers = new HashMap<>();
            String currentHeader;
            while ((currentHeader = reader.readLine()) != null && !currentHeader.isEmpty()) {
                // Split header name and value on ':' and add to map of header name(s) -> values.
                String[] splitHeader = currentHeader.split(":");
                headers.put(splitHeader[0].toLowerCase(), splitHeader[1].trim());
            }

            Optional<Integer> contentLength = getHeader(headers, CONTENT_LENGTH_HEADER_KEY, Integer::valueOf);
            Optional<HttpContentType> contentType = getHeader(headers, CONTENT_TYPE_HEADER_KEY, HttpContentType::valueOf);
            Optional<HttpTransferEncoding> transferEncoding = getHeader(headers, TRANSFER_ENCODING_HEADER_KEY, HttpTransferEncoding::valueOf);

            // Retrieve body and http method for request
            HttpRequestBody body = readBody(reader, contentType.orElse(null), transferEncoding.orElse(null), contentLength.orElse(null));
            HttpMethod httpMethod = HttpMethod.getMethod(httpDetails[0]);

            // Create request pojo and populate values
            HttpRequest request = HttpRequest.builder()
                    .method(httpMethod)
                    .uri(httpDetails[1])
                    .protocol(httpDetails[2])
                    .queryParams(queryParams)
                    .headers(headers)
                    .body(body)
                    .build();

            return Optional.of(request);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an optional containing the retrieved header value if present using the provided headerKey.
     *
     * @param headers Map of header key to values.
     * @param headerKey String of header key.
     * @param valueOfFunc Function to convert the retrieved string header value to a specific data type;
     * @return Optional containing either the retrieved value or empty if not.
     * @param <T> Generic value representing the converted type of the retrieved header value.
     */
    public static <T> Optional<T> getHeader(Map<String, String> headers, String headerKey, Function<String, T> valueOfFunc) {
        Optional<String> rawHeaderValue = Optional.ofNullable(headers.get(headerKey));
        if (!rawHeaderValue.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(valueOfFunc.apply(rawHeaderValue.get()));
        } catch (RuntimeException e) {
            Logging.LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Reads and returns an HTTP request body from a provided reader and header values.
     */
    private static HttpRequestBody readBody(BufferedReader reader, HttpContentType contentType,
                                            HttpTransferEncoding transferEncoding, Integer contentLength) {
        HttpRequestBody requestBody = null;

        if (contentLength != null && contentType != null) {
            // TODO: add support for additional content types
            requestBody = readPlainBody(reader, contentLength);
        }

        // TODO: implement ability to read body without content length header present.

        return requestBody;
    }

    // Reads an HTTP request body of plain content type into an HttpPlainRequestBody.

    /**
     * Reads an HTTP request body of plain content type into an HttpPlainRequestBody.
     *
     * @param reader Reader containing the raw byte data of the http request being read.
     * @param bodyByteLength Integer representing the length in bytes of the request body.
     * @return HttpPlainRequestBody containing the string representation of the request's data.
     */
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

    /**
     * Parses the query params belonging to the provided URI if present
     *
     * @param uri String representation of full URI
     * @return Map containing a query param name to its corresponding query param value
     */
    private static Map<String, String> getQueryParamsFromURI(String uri) {
        Map<String, String> queryParams = new HashMap<>();

        if (uri.contains("?")) {
            String uriSplit = uri.split("\\?")[1];
            String[] rawQueryParams = uriSplit.split("&");

            for (String rawQueryParam : rawQueryParams) {
                String[] paramSplit = rawQueryParam.split("=");
                queryParams.put(paramSplit[0], paramSplit[1]);
            }
        }

        return queryParams;
    }

}
