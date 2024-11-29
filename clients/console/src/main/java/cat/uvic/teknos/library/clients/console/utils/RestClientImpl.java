package cat.uvic.teknos.library.clients.console.utils;

import cat.uvic.teknos.library.clients.console.exceptions.ConsoleClientException;
import cat.uvic.teknos.library.clients.console.exceptions.RequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.*;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyStore;

public class RestClientImpl implements RestClient {

    private final int port;
    private final String host;
    private final RawHttp rawHttp = new RawHttp();
    private final ObjectMapper objectMapper = Mappers.get();
    private SSLSocketFactory sslSocketFactory;

    public RestClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
        setupSSL();
    }

    // Setup SSL context for the connection
    private void setupSSL() {
        try {
            // Load the keystore (client's private key and certificate)
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream keystoreStream = new FileInputStream("client.p12")) {
                keyStore.load(keystoreStream, "password".toCharArray());
            }

            // Create KeyManagerFactory for client authentication
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "password".toCharArray());

            // Load the truststore (server's public certificate)
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream truststoreStream = new FileInputStream("client-truststore.jks")) {
                trustStore.load(truststoreStream, "password".toCharArray());
            }

            // Create TrustManagerFactory to verify the server certificate
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            // Setup the SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            sslSocketFactory = sslContext.getSocketFactory();

        } catch (Exception e) {
            throw new RuntimeException("Error setting up SSL: " + e.getMessage(), e);
        }
    }

    // Use SSL socket to connect securely
    private SSLSocket createSSLSocket() throws IOException {
        return (SSLSocket) sslSocketFactory.createSocket(host, port);
    }

    @Override
    public <T> T get(String path, Class<T> returnType) throws RequestException {
        return execRequest("GET", path, null, returnType);
    }

    @Override
    public <T> T[] getAll(String path, Class<T[]> returnType) throws RequestException {
        return execRequest("GET", path, null, returnType);
    }

    @Override
    public void post(String path, String body) throws RequestException {
        execRequest("POST", path, body, Void.class);
    }

    @Override
    public void put(String path, String body) throws RequestException {
        execRequest("PUT", path, body, Void.class);
    }

    @Override
    public void delete(String path) throws RequestException {
        execRequest("DELETE", path, null, Void.class);
    }

    private <T> T execRequest(String method, String path, String body, Class<T> returnType) throws RequestException {
        try (SSLSocket socket = createSSLSocket()) {
            String requestBody = body == null ? "" : body;

            // Build and send the request
            RawHttpRequest request = rawHttp.parseRequest(
                    method + " " + path + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + requestBody.length() + "\r\n\r\n" +
                            requestBody
            );
            request.writeTo(socket.getOutputStream());

            // Parse the response
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() >= 400) {
                String errorBody = response.getBody()
                        .map(bodyReader -> {
                            try {
                                return bodyReader.asRawBytes();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                        .orElse("No error details provided.");
                throw new RequestException("Request failed with status: " + response.getStatusCode() + ". Details: " + errorBody);
            }

            if (returnType.equals(Void.class)) {
                return null; // No response expected
            }

            String responseBody = response.getBody()
                    .map(bodyReader -> {
                        try {
                            return bodyReader.asRawBytes();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                    .orElse("");

            return responseBody.isEmpty() ? null : objectMapper.readValue(responseBody, returnType);

        } catch (IOException e) {
            throw new RequestException("Network error occurred during the request.", e);
        } catch (Exception e) {
            throw new RequestException("Unexpected error during request execution.", e);
        }
    }
}
