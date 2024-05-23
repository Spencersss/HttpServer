package dev.spence.http.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HttpPlainRequestBody implements HttpRequestBody {

    private final String content;

    @Override
    public byte[] toBytes() {
        return content.getBytes();
    }
}
