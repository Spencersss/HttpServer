package dev.spence.server;

import dev.spence.http.HttpHandler;
import dev.spence.http.HttpRouteHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    // Vars
    private final Map<String, HttpRouteHandler> routes = new HashMap<>();
    private final ExecutorService executor;
    private final ServerSocket socket;
    private HttpHandler httpHandler;

    // Default configuration
    public HttpServer() throws IOException {
        this.executor = Executors.newFixedThreadPool(20);
        this.socket = new ServerSocket(80);
    }

    // Configurable
    public HttpServer(int port, int threads) throws IOException {
        this.executor = Executors.newFixedThreadPool(threads);
        this.socket = new ServerSocket(port);
    }

    public void start() throws IOException {
        httpHandler = new HttpHandler(routes);

        Logging.LOGGER.info(String.format("HTTP Server Started on %s:%o",
                socket.getInetAddress().getHostAddress(), socket.getLocalPort()));

        try {
            // Continue to accept requests until our thread pool is shutdown entirely
            while (!executor.isShutdown()) {
                // Accept connection and pass to handler method
                handleIncomingConnection(this.socket.accept());
            }
        } finally {
            executor.shutdown();
        }

    }

    /**
     * Adds a new route and associated route handler if it does not exist to our enabled list of routes.
     *
     * @param route String value of our route.
     * @param routeHandler HttpRouteHandler that will handle incoming requests to the established route.
     */
    public void addRoute(String route, HttpRouteHandler routeHandler) {
        if (!this.routes.containsKey(route)) {
            this.routes.put(route, routeHandler);
        }
    }

    /**
     * Creates a single pooled thread for handling an individual socket connection by the http handler.
     *
     * @param incomingConnection Socket connection containing expected http request.
     */
    private void handleIncomingConnection(Socket incomingConnection) {
        executor.execute(() -> {
            try {
                httpHandler.handleConnection(incomingConnection);
            } catch (IOException ignored) {}
        });
    }

}
