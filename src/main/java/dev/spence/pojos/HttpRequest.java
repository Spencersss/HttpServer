package dev.spence.pojos;

import dev.spence.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> headers;

    public String toRequestKey() {
        return method.toString() + uri;
    }

}

