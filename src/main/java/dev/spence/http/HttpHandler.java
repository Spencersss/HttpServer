package dev.spence.http;

import dev.spence.http.request.HttpRequest;
import dev.spence.http.response.HttpResponse;
import dev.spence.server.Logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHandler {

    private final Map<String, HttpRouteHandler> routes;

    public HttpHandler(Map<String, HttpRouteHandler> routes) {
        this.routes = routes;
    }

    public void handleConnection(Socket connection) throws IOException {
        // Get input & output streams to read connection input and write response later on
        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = connection.getOutputStream();

        // Decode request and map to HttpRequest pojo
        Optional<HttpRequest> decodedRequest = HttpDecoder.decode(inputStream);

        // Verify route exists and handle
        HttpResponse response;
        if (decodedRequest.isPresent()) {
            HttpRequest request = decodedRequest.get();
            Optional<HttpRouteHandler> routeHandler = Optional.ofNullable(
                    routes.get(request.toRequestKey()));

            if (routeHandler.isPresent()) {
                Logging.LOGGER.info(String.format("Received from %s to %s %s",
                        connection.getInetAddress().getHostAddress(),
                        request.getMethod().toString(),
                        request.getUri()));
                response = routeHandler.get().handle(decodedRequest.get());
            } else {
                response = HttpResponse.notFound();
            }
        } else {
            // Unknown to decode a properly constructed HTTP request, send bad request response.
            response = HttpResponse.badRequest();
        }

        // Write response to output stream
        List<Byte> encodedResponse = HttpEncoder.encode(response);
        HttpResponseWriter.writeResponseBytesToOutput(outputStream, encodedResponse);

        // Close active streams
        inputStream.close();
        outputStream.close();

        Logging.LOGGER.info("Response sent!");
    }

}
