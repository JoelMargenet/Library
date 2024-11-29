package cat.library.services;

import rawhttp.core.RawHttp;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
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
    private SSLServerSocketFactory sslServerSocketFactory;

    public Server(RequestRouter requestRouter) {
        this.requestRouter = requestRouter;
    }

    public void start() {
        threadPool = Executors.newFixedThreadPool(10);
        scheduler = Executors.newSingleThreadExecutor();
        scheduler.submit(this::monitorShutdown);

        try {
            // Load keystore
            KeyStore keystore = loadKeystore("server.p12", "password");

            // Set up KeyManagerFactory for server authentication (server's private key)
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, "password".toCharArray());

            // Set up TrustManagerFactory for client verification (server verifies client)
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keystore);

            // Set up the SSLContext with the KeyManager and TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());

            // Create an SSLServerSocketFactory
            sslServerSocketFactory = sslContext.getServerSocketFactory();

            System.out.println("Server started on port " + PORT);
            while (!shutdownServer) {
                try {
                    // Create SSL socket instead of plain ServerSocket
                    var sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);
                    var clientSocket = sslServerSocket.accept();
                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (shutdownServer) {
                        System.out.println("Server shutdown initiated...");
                        break;
                    }
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to start server", e);
        } finally {
            shutdown();
        }
    }

    private void handleClient(java.net.Socket clientSocket) {
        try (clientSocket) {
            System.out.println("Handling client request on thread: " + Thread.currentThread().getName());
            var rawHttp = new RawHttp();
            var request = rawHttp.parseRequest(clientSocket.getInputStream()).eagerly();
            var response = requestRouter.execRequest(request);
            response.writeTo(clientSocket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }

    private KeyStore loadKeystore(String keystorePath, String password) throws Exception {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keystore.load(fis, password.toCharArray());
        }
        return keystore;
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

