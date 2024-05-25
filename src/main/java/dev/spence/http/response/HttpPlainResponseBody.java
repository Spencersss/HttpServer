package dev.spence.http.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HttpPlainResponseBody implements HttpResponseBody {

    private final String contents;

    @Override
    public byte[] toBytes() {
        return contents.getBytes();
    }
}
