package dev.spence;

import dev.spence.http.request.HttpRequest;
import dev.spence.http.response.HttpResponse;
import dev.spence.server.HttpServer;

import java.io.IOException;

public class Server {

    public static void main(String[] args) {
        try {
            HttpServer httpServer = new HttpServer(80, 20);
            httpServer.addRoute("GET/test", (request, params) -> HttpResponse.success("Success body"));
            httpServer.addRoute("GET/file", ((request, params) -> getFileRoute(request, "filename_placeholder")));
            httpServer.start();
        } catch (IOException e) {
            // Unable to start server, simply exit
            System.exit(0);
        }
    }

    // GET /file
    private static HttpResponse getFileRoute(HttpRequest req, String filename) {
        return HttpResponse.success("Got a file!");
    }

}