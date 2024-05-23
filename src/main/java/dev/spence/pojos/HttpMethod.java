package dev.spence.pojos;

public enum HttpMethod {

    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    UNKNOWN;

    public static HttpMethod getMethod(String methodName) {
        for (HttpMethod method : HttpMethod.values()) {
            if (method.name().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        return UNKNOWN;
    }
}
