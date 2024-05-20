package dev.spence.http;

import dev.spence.pojos.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

public class HttpHandler {

    private final Map<String, Runnable> routes;

    public HttpHandler(Map<String, Runnable> routes) {
        this.routes = routes;
    }

    public void handleConnection(Socket connection) throws IOException {
        // Get input & output streams to read connection input and write response later on
        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = connection.getOutputStream();

        // Decode request and map to HttpRequest pojo
        Optional<HttpRequest> request = HttpDecoder.decode(inputStream);
    }

}
