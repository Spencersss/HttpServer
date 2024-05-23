package dev.spence.http;

import dev.spence.http.request.HttpRequest;
import dev.spence.http.response.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

public class HttpHandler {

    private final Map<String, HttpRouteHandler<HttpRequest>> routes;

    public HttpHandler(Map<String, HttpRouteHandler<HttpRequest>> routes) {
        this.routes = routes;
    }

    public void handleConnection(Socket connection) throws IOException {
        // Get input & output streams to read connection input and write response later on
        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = connection.getOutputStream();

        // Decode request and map to HttpRequest pojo
        Optional<HttpRequest> request = HttpDecoder.decode(inputStream);

        // Verify route exists and handle
        HttpResponse response;
        if (request.isPresent()) {
            Optional<HttpRouteHandler<HttpRequest>> routeHandler = Optional.ofNullable(
                    routes.get(request.get().toRequestKey()));

            if (routeHandler.isPresent()) {
                response = routeHandler.get().handle(request.get());
            }
        } else {
            // Unknown route, build invalid response
            response = HttpResponse.builder().build();
        }

        // Write response to output stream

    }

}
