package cat.uvic.teknos.library.clients.console.utils;

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

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper = Mappers.get(); // Use configured ObjectMapper
    private final RawHttp rawHttp = new RawHttp();
    private static final String KEYSTORE_PATH = "/client1.p12";
    private static final String KEYSTORE_PASSWORD = "Teknos01.";
    private static final String SERVER_CERT_ALIAS = "server";

    private final PublicKey serverPublicKey;

    public RestClientImpl(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream keyStoreStream = getClass().getResourceAsStream(KEYSTORE_PATH);

            if (keyStoreStream == null) {
                throw new FileNotFoundException("Keystore file not found in classpath: " + KEYSTORE_PATH);
            }

            keyStore.load(keyStoreStream, KEYSTORE_PASSWORD.toCharArray());
            serverPublicKey = keyStore.getCertificate(SERVER_CERT_ALIAS).getPublicKey();

            if (serverPublicKey == null) {
                throw new RuntimeException("Failed to load the server's public key.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading client keystore: " + e.getMessage(), e);
        }
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
            String requestBody = body == null ? "" : body;

            // Generate a symmetric key
            SecretKey symmetricKey = CryptoUtils.createSecretKey();
            System.out.println("Generated symmetric key: " + CryptoUtils.toBase64(symmetricKey.getEncoded()));

            // Encrypt the symmetric key using the server's public key
            String encryptedSymmetricKeyBase64 = CryptoUtils.asymmetricEncrypt(CryptoUtils.toBase64(symmetricKey.getEncoded()), serverPublicKey);
            System.out.println("Encrypted symmetric key (Base64): " + encryptedSymmetricKeyBase64);

            // Encrypt the request body
            if (!requestBody.isEmpty()) {
                requestBody = CryptoUtils.encrypt(requestBody, symmetricKey);
                System.out.println("Encrypted request body: " + requestBody);
            }

            // Calculate the hash of the request body (used to ensure integrity)
            String requestBodyHash = CryptoUtils.getHash(requestBody);
            System.out.println("Request body hash: " + requestBodyHash);

            // Build the HTTP request
            RawHttpRequest request = rawHttp.parseRequest(
                    method + " " + path + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + requestBody.length() + "\r\n" +
                            "Symmetric-Key: " + encryptedSymmetricKeyBase64 + "\r\n" +
                            "Body-Hash: " + requestBodyHash + "\r\n\r\n" +
                            requestBody
            );

            // Send the request
            request.writeTo(socket.getOutputStream());

            // Get the response
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() >= 400) {
                throw new RequestException("Request failed with status: " + response.getStatusCode());
            }

            // Decrypt the response body (if any)
            String responseBody = response.getBody()
                    .map(bodyReader -> {
                        try {
                            return new String(bodyReader.asRawBytes(), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse("");

            System.out.println("Raw response body: " + responseBody);

            if (!responseBody.isEmpty()) {
                responseBody = CryptoUtils.decrypt(responseBody, symmetricKey);
                System.out.println("Decrypted response body: " + responseBody);
            }

            return responseBody.isEmpty() ? null : objectMapper.readValue(responseBody, returnType);

        } catch (Exception e) {
            throw new RequestException("Error during request execution", e);
        }
    }
}
