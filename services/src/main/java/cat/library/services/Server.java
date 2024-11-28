package cat.library.services;

import cat.library.services.exception.ServerException;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    public static final int PORT = 8080;
    private final RequestRouter requestRouter;
    private volatile boolean shutdownServer = false;
    private static final String PROPERTIES_PATH = "services/src/main/resources/server.properties";

    private ExecutorService threadPool;
    private ExecutorService scheduler;
    private ServerSocket serverSocket;

    public Server(RequestRouter requestRouter) {
        this.requestRouter = requestRouter;
    }

    public void start() {
        threadPool = Executors.newFixedThreadPool(10);
        scheduler = Executors.newSingleThreadExecutor();

        scheduler.submit(this::monitorShutdown);

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (!shutdownServer) {
                try {
                    var clientSocket = serverSocket.accept();
                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (shutdownServer) {
                        System.out.println("Server shutdown initiated...");
                        break;
                    }
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ServerException("Failed to start server", e);
        } finally {
            shutdown();
        }
    }

    private void handleClient(java.net.Socket clientSocket) {
        try (clientSocket) {
            System.out.println("Handling client request on thread: " + Thread.currentThread().getName());

            var rawHttp = new RawHttp(RawHttpOptions.newBuilder()
                    .doNotInsertHostHeaderIfMissing()
                    .build());
            var request = rawHttp.parseRequest(clientSocket.getInputStream()).eagerly();
            var response = requestRouter.execRequest(request);
            response.writeTo(clientSocket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }


    private void monitorShutdown() {
        while (!shutdownServer) {
            checkShutdownProperty();
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void checkShutdownProperty() {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(PROPERTIES_PATH)) {
            properties.load(fis);
            String shutdownValue = properties.getProperty("shutdown");

            if ("true".equalsIgnoreCase(shutdownValue)) {
                System.out.println("Terminating server...");
                shutdownServer = true;

                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        System.err.println("Error closing server socket: " + e.getMessage());
                    }
                }

                shutdownServices();
            }
        } catch (IOException e) {
            System.err.println("Error reading server.properties: " + e.getMessage());
        }
    }

    private void shutdownServices() {
        try {
            threadPool.shutdownNow();
            scheduler.shutdownNow();
        } catch (Exception e) {
            System.err.println("Error shutting down services: " + e.getMessage());
        }
    }

    private void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
