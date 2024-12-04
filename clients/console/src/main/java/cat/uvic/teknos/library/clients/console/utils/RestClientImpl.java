package cat.uvic.teknos.library.clients.console.utils;

import cat.uvic.teknos.library.clients.console.exceptions.ConsoleClientException;
import cat.uvic.teknos.library.clients.console.exceptions.RequestException;
import cat.uvic.teknos.library.clients.CryptoUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.*;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PublicKey;

public class RestClientImpl implements RestClient {
    private final int port;
    private final String host;
    private final RawHttp rawHttp = new RawHttp();
    private final ObjectMapper objectMapper = Mappers.get();
    private final PublicKey serverPublicKey;

    private static final String KEYSTORE_PATH = "/client1.p12";
    private static final String KEYSTORE_PASSWORD = "Teknos01.";
    private static final String KEY_ALIAS = "server";

    public RestClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
        this.serverPublicKey = loadServerPublicKey();
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
        try (Socket socket = new Socket(host, port)) {
            // Generate a symmetric key
            SecretKey symmetricKey = CryptoUtils.createSecretKey();

            // Encrypt the symmetric key using the server's public key
            String encryptedSymmetricKey = CryptoUtils.asymmetricEncrypt(CryptoUtils.toBase64(symmetricKey.getEncoded()), serverPublicKey);

            // Encrypt the request body (if applicable) and compute the hash
            String requestBody = body == null ? "" : body;
            String encryptedRequestBody = requestBody.isEmpty() ? "" : CryptoUtils.encrypt(requestBody, symmetricKey);
            String bodyHash = CryptoUtils.getHash(encryptedRequestBody);

            // Build and send the request with encryption headers
            RawHttpRequest request = rawHttp.parseRequest(
                    method + " " + path + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + encryptedRequestBody.length() + "\r\n" +
                            "Symmetric-Key: " + encryptedSymmetricKey + "\r\n" +
                            "Body-Hash: " + bodyHash + "\r\n\r\n" +
                            encryptedRequestBody
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

            // Decrypt the response body
            String decryptedResponseBody = responseBody.isEmpty() ? "" : CryptoUtils.decrypt(responseBody, symmetricKey);

            return decryptedResponseBody.isEmpty() ? null : objectMapper.readValue(decryptedResponseBody, returnType);

        } catch (IOException e) {
            throw new RequestException("Network error occurred during the request.", e);
        } catch (Exception e) {
            throw new RequestException("Unexpected error during request execution.", e);
        }
    }

    private PublicKey loadServerPublicKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream keyStoreStream = getClass().getResourceAsStream(KEYSTORE_PATH)) {
                if (keyStoreStream == null) {
                    throw new FileNotFoundException("Keystore file not found in classpath: " + KEYSTORE_PATH);
                }
                keyStore.load(keyStoreStream, KEYSTORE_PASSWORD.toCharArray());
            }

            return keyStore.getCertificate(KEY_ALIAS).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Error loading server public key: " + e.getMessage(), e);
        }
    }
}
