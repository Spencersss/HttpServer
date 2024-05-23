package dev.spence.http.request;

import dev.spence.pojos.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> headers;
    private final HttpRequestBody body;

    public String toRequestKey() {
        return method.toString() + uri;
    }

}

