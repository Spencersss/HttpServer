package dev.spence.http;

import dev.spence.http.response.HttpResponse;

public class HttpEncoder {

    // Encodes a provided http response into a byte array to be sent across a OutputStream
    public static byte[] encode(HttpResponse response) {
        //CharBuffer charBuffer = CharBuffer.allocate(response.get);
        return new byte[1];
    }

}
