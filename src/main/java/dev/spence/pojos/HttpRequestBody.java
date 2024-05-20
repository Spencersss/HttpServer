package dev.spence.pojos;

import lombok.Data;

@Data
public class HttpRequestBody {

    private final String content;

    public HttpRequestBody(String bodyString) {
        this.content = bodyString;
    }

}
