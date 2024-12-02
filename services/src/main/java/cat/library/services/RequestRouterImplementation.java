package cat.library.services;

import cat.library.services.controllers.Controller;
import cat.library.services.exception.ResourceNotFoundException;
import cat.library.services.exception.ServerErrorException;
import cat.uvic.teknos.library.clients.test.CryptoUtils;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Map;

public class RequestRouterImplementation implements RequestRouter {

    private static final RawHttp rawHttp = new RawHttp();
    private final Map<String, Controller> controllers;
    private final CryptoUtils cryptoUtils = new CryptoUtils();
    private final PrivateKey serverPrivateKey;

    public RequestRouterImplementation(Map<String, Controller> controllers) {
        this.controllers = controllers;

        try {
            // Load server keystore from resources
            KeyStore serverKeyStore = KeyStore.getInstance("PKCS12");
            try (InputStream keyStoreStream = RequestRouterImplementation.class.getResourceAsStream("/server.p12")) {
                if (keyStoreStream == null) {
                    throw new RuntimeException("Server keystore not found.");
                }
                serverKeyStore.load(keyStoreStream, "Teknos01.".toCharArray());
            }

            // Get the server's private key
            this.serverPrivateKey = (PrivateKey) serverKeyStore.getKey("server", "Teknos01.".toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load server keystore or private key", e);
        }
    }

    @Override
    public RawHttpResponse<?> execRequest(RawHttpRequest request) {
        String path = request.getUri().getPath();
        String method = request.getMethod();
        String[] pathParts = path.split("/");

        if (pathParts.length < 2) {
            return buildErrorResponse(404, "Invalid request path");
        }

        String controllerName = pathParts[1];
        String responseJsonBody = "";

        try {
            // Handle request encryption and validation
            String encryptedKey = getHeaderValue(request, "Encrypted-Key");
            String bodyHash = getHeaderValue(request, "Body-Hash");

            // Decrypt the symmetric key
            String symmetricKeyBase64 = cryptoUtils.asymmetricDecrypt(encryptedKey, serverPrivateKey);
            SecretKey symmetricKey = cryptoUtils.decodeSecretKey(symmetricKeyBase64);

            // Decrypt the body
            String encryptedBody = decryptRequestBody(request);
            String decryptedBody = encryptedBody.isEmpty() ? "" : cryptoUtils.decrypt(encryptedBody, symmetricKey);

            // Validate body hash
            validateBodyHash(decryptedBody, bodyHash);

            // Process the request based on the controller name
            responseJsonBody = handleRequest(controllerName, method, decryptedBody, pathParts);

            // Encrypt the response body
            String encryptedResponseBody = cryptoUtils.encrypt(responseJsonBody, symmetricKey);

            return buildSuccessResponse(encryptedResponseBody);

        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(404, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private String getHeaderValue(RawHttpRequest request, String header) {
        return request.getHeaders().get(header)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Missing " + header + " header"));
    }

    private String decryptRequestBody(RawHttpRequest request) {
        return request.getBody()
                .map(bodyReader -> {
                    try {
                        return bodyReader.decodeBodyToString(StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read request body", e);
                    }
                })
                .orElse("");
    }

    private void validateBodyHash(String decryptedBody, String expectedHash) {
        if (!decryptedBody.isEmpty()) {
            String calculatedHash = cryptoUtils.getHash(decryptedBody);
            if (!calculatedHash.equals(expectedHash)) {
                throw new ResourceNotFoundException("Body hash mismatch");
            }
        }
    }

    private RawHttpResponse<?> buildSuccessResponse(String encryptedResponseBody) {
        return rawHttp.parseResponse("HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + encryptedResponseBody.length() + "\r\n" +
                "\r\n" +
                encryptedResponseBody);
    }

    private RawHttpResponse<?> buildErrorResponse(int statusCode, String errorMessage) {
        return rawHttp.parseResponse("HTTP/1.1 " + statusCode + " " + (statusCode == 404 ? "Not Found" : "Internal Server Error") + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" + errorMessage);
    }

    private String handleRequest(String controllerName, String method, String body, String[] pathParts) {
        Controller controller = controllers.get(controllerName);
        if (controller == null) {
            throw new ResourceNotFoundException("Controller not found for: " + controllerName);
        }

        try {
            if ("GET".equals(method)) {
                if (pathParts.length == 3) {
                    return controller.get(Integer.parseInt(pathParts[2]));
                } else {
                    return controller.get();
                }
            } else if ("POST".equals(method)) {
                controller.post(body);
            } else if ("PUT".equals(method) && pathParts.length == 3) {
                controller.put(Integer.parseInt(pathParts[2]), body);
            } else if ("DELETE".equals(method) && pathParts.length == 3) {
                return controller.delete(Integer.parseInt(pathParts[2]));
            }
        } catch (Exception e) {
            throw new ServerErrorException("Failed to handle request", e);
        }

        return "";
    }
}
