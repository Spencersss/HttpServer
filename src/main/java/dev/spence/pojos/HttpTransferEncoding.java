package dev.spence.pojos;

import lombok.Getter;

@Getter
public enum HttpTransferEncoding {

    CHUNKED("chunked"),
    COMPRESS("compress"),
    DEFLATE("deflate"),
    GZIP("gzip"),
    UNKNOWN("");

    private final String encodingString;

    HttpTransferEncoding(String encodingString) {
        this.encodingString = encodingString;
    }

    public static HttpTransferEncoding getEncoding(String encoding) {
        for (HttpTransferEncoding transferEncoding : HttpTransferEncoding.values()) {
            if (transferEncoding.getEncodingString().equalsIgnoreCase(encoding)) {
                return transferEncoding;
            };
        }
        return UNKNOWN;
    }

}
