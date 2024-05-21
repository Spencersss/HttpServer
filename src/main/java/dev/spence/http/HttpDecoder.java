package dev.spence.http;

import dev.spence.pojos.HttpMethod;
import dev.spence.pojos.HttpRequest;
import dev.spence.pojos.HttpRequestBody;
import dev.spence.server.Logging;

import java.io.*;
import java.util.*;

public class HttpDecoder {

    public static String CHUNK_ENCODING_DELIMITER = "\r\n";
    public static String CONTENT_LENGTH_HEADER_KEY = "content-length";
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

        // Retrieve body of request
        String bodyString = "";
        Optional<Integer> contentLength = Optional.ofNullable(headers.get(CONTENT_LENGTH_HEADER_KEY)).map(Integer::valueOf);

        if (contentLength.isPresent()) {
            // We have a defined byte length for the body, we can convert this to the expected number of
            // characters to be read from the stream before there is no data remaining.
            // Assuming UTF-8 encoding, content length is 1:1 meaning 1 byte represents 1 char.
            bodyString = readBody(reader, contentLength.get());
        } else {
            // Length of body is unknown as such, we support transfer of said data using chunk encoding
            String encodingType = headers.get(TRANSFER_ENCODING_HEADER_KEY);
            if (encodingType.equalsIgnoreCase("chunked")) {
                bodyString = readChunkBody(reader);
            }
        }

        HttpRequestBody body = new HttpRequestBody(bodyString);

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

    /**
     * Reads an HTTP request body with a defined body byte length from a provided reader assuming
     * headers and request itself have already been read.
     */
    private static String readBody(BufferedReader reader, int bodyByteLength) {
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
            };
        }

        return Arrays.toString(bodyBuffer);
    }

    /**
     * Reads an HTTP request body using chunked transfer encoding from a provided reader assuming
     * headers and request itself have already been read.
     */
    private static String readChunkBody(BufferedReader reader) {
        // TODO: implement reading of body using chunked transfer encoding
        return "";
    }

}
