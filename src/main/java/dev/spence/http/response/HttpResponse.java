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

}