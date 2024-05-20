package dev.spence.server;

import dev.spence.http.HttpHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    // Vars
    private final Map<String, Runnable> routes = new HashMap<>();
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

    // Creates a single thread for handling an individual socket connection (and request being sent along)
    private void handleIncomingConnection(Socket incomingConnection) {
        executor.execute(() -> {
            try {
                httpHandler.handleConnection(incomingConnection);
            } catch (IOException ignored) {}
        });
    }

}
