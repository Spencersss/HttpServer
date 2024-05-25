package dev.spence;

import dev.spence.http.response.HttpResponse;
import dev.spence.server.HttpServer;

import java.io.IOException;

public class Server {

    public static void main(String[] args) {
        try {
            HttpServer httpServer = new HttpServer(80, 20);
            httpServer.addRoute("GET/test", (request, params) -> HttpResponse.success("Success body"));
            httpServer.start();
        } catch (IOException e) {
            // Unable to start server, simply exit
            System.exit(0);
        }
    }

}