package dev.spence.http.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;

@Getter
@Builder
@AllArgsConstructor
public class HttpBinaryResponseBody implements HttpResponseBody {

    private final byte[] contents;
    private final String contentType;

    @Override
    public byte[] toBytes() {
        return contents;
    }

    @Override
    public String toString() {
        return Arrays.toString(contents);
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }
}
