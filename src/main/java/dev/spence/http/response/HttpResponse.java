package dev.spence.http.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HttpResponse {

    private final String protocol;
    private final int statusCode;
    private final String statusText;
    private final Map<String, String> headers;
    private final HttpResponseBody body;

    private static final String DEFAULT_SUPPORTED_PROTOCOL = "HTTP/1.1";

    // Templates for commonly used responses
    public static HttpResponse success(String bodyString) {
        return HttpResponse.builder()
                .protocol(DEFAULT_SUPPORTED_PROTOCOL)
                .statusCode(200)
                .statusText("Success")
                .body(HttpPlainResponseBody.builder().contents(bodyString).build())
                .build();
    }

    public static HttpResponse badRequest() {
        return HttpResponse.builder()
                .protocol(DEFAULT_SUPPORTED_PROTOCOL)
                .statusCode(400)
                .statusText("Bad Request")
                .body(HttpPlainResponseBody.builder().contents("").build())
                .build();
    }

    public static HttpResponse notFound() {
        return HttpResponse.builder()
                .protocol(DEFAULT_SUPPORTED_PROTOCOL)
                .statusCode(404)
                .statusText("Not Found")
                .body(HttpPlainResponseBody.builder().contents("Route not found.").build())
                .build();
    }

}
