package dev.spence.pojos;

import lombok.Getter;

@Getter
public enum HttpContentType {

    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_PDF("application/pdf"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    UNKNOWN("");

    private final String contentTypeString;

    HttpContentType(String contentTypeString) {
        this.contentTypeString = contentTypeString;
    }

    public static HttpContentType getType(String type) {
        for (HttpContentType contentType : HttpContentType.values()) {
            if (contentType.getContentTypeString().equalsIgnoreCase(type)) {
                return contentType;
            };
        }
        return UNKNOWN;
    }

}
