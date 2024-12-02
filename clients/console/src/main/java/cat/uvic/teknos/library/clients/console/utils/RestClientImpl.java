package cat.uvic.teknos.library.clients.console.utils;

import cat.uvic.teknos.library.clients.console.exceptions.RequestException;
import cat.uvic.teknos.library.clients.test.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.*;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RestClientImpl implements RestClient {
    private final int port;
    private final String host;
    private final RawHttp rawHttp = new RawHttp();
    private final ObjectMapper objectMapper = Mappers.get();
    private final CryptoUtils cryptoUtils = new CryptoUtils();

    public RestClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
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

            SecretKey symmetricKey = cryptoUtils.createSecretKey();
            String encryptedSymmetricKey = encryptSymmetricKey(symmetricKey);

            String bodyHash = cryptoUtils.getHash(requestBody);

            String encryptedBody = cryptoUtils.encrypt(requestBody, symmetricKey);

            RawHttpRequest request = rawHttp.parseRequest(
                    method + " " + path + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Encrypted-Key: " + encryptedSymmetricKey + "\r\n" +
                            "Body-Hash: " + bodyHash + "\r\n" +
                            "Content-Length: " + encryptedBody.length() + "\r\n\r\n" +
                            encryptedBody
            );
            request.writeTo(socket.getOutputStream());

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

            String decryptedResponseBody = responseBody.isEmpty() ? "" : cryptoUtils.decrypt(responseBody, symmetricKey);

            if (returnType.equals(Void.class)) {
                return null;
            }

            return decryptedResponseBody.isEmpty() ? null : objectMapper.readValue(decryptedResponseBody, returnType);

        } catch (IOException e) {
            throw new RequestException("Network error occurred during the request.", e);
        } catch (Exception e) {
            throw new RequestException("Unexpected error during request execution.", e);
        }
    }

    public String encryptSymmetricKey(SecretKey symmetricKey) throws RequestException {
        try {
            String publicKeyBase64 = "Teknos01.";

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            return cryptoUtils.asymmetricEncrypt(Base64.getEncoder().encodeToString(symmetricKey.getEncoded()), publicKey);
        } catch (Exception e) {
            throw new RequestException("Error encrypting the symmetric key", e);
        }
    }

}
