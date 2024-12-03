package cat.library.services;

import cat.library.services.controllers.Controller;
import cat.library.services.exception.ResourceNotFoundException;
import cat.library.services.exception.ServerErrorException;
import cat.uvic.teknos.library.clients.CryptoUtils;
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
import java.util.logging.Logger;

public class RequestRouterImplementation implements RequestRouter {

    private static final RawHttp rawHttp = new RawHttp();
    private final Map<String, Controller> controllers;
    private final CryptoUtils cryptoUtils = new CryptoUtils();
    private final PrivateKey serverPrivateKey;
    private static final Logger logger = Logger.getLogger(RequestRouterImplementation.class.getName());

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
            logger.info("Encrypted key: " + encryptedKey);

            // Decrypt the symmetric key
            String symmetricKeyBase64 = cryptoUtils.asymmetricDecrypt(encryptedKey, serverPrivateKey);
            logger.info("Decrypted symmetric key base64: " + symmetricKeyBase64);

            SecretKey symmetricKey = cryptoUtils.decodeSecretKey(symmetricKeyBase64);
            logger.info("Symmetric key: " + symmetricKey);

            // Decrypt the body
            String encryptedBody = decryptRequestBody(request);
            logger.info("Encrypted body: " + encryptedBody);
            String decryptedBody = encryptedBody.isEmpty() ? "" : cryptoUtils.decrypt(encryptedBody, symmetricKey);
            logger.info("Decrypted body: " + decryptedBody);

            // Process the request based on the controller name
            responseJsonBody = handleRequest(controllerName, method, decryptedBody, pathParts);
            logger.info("Response body: " + responseJsonBody);

            // Encrypt the response body
            String encryptedResponseBody = cryptoUtils.encrypt(responseJsonBody, symmetricKey);
            logger.info("Encrypted response body: " + encryptedResponseBody);

            return buildSuccessResponse(encryptedResponseBody);

        } catch (ResourceNotFoundException e) {
            logger.severe("Resource not found: " + e.getMessage());
            return buildErrorResponse(404, e.getMessage());
        } catch (Exception e) {
            logger.severe("Internal Server Error: " + e.getMessage());
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
                return ""; // POST does not usually return a body, so we can return an empty response
            } else if ("PUT".equals(method) && pathParts.length == 3) {
                controller.put(Integer.parseInt(pathParts[2]), body);
                return ""; // PUT may not return a body, so return empty string
            } else if ("DELETE".equals(method) && pathParts.length == 3) {
                return controller.delete(Integer.parseInt(pathParts[2]));
            }
        } catch (Exception e) {
            throw new ServerErrorException("Failed to handle request", e);
        }

        return "";
    }
}
