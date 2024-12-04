package cat.library.services;

import cat.library.services.controllers.Controller;
import cat.library.services.exception.ResourceNotFoundException;
import cat.library.services.exception.ServerErrorException;
import cat.uvic.teknos.library.clients.CryptoUtils;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
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
    private static final String KEYSTORE_PATH = "/server.p12";
    private static final String KEYSTORE_PASSWORD = "Teknos01.";
    private static final String KEY_ALIAS = "server";
    private final PrivateKey serverPrivateKey;

    public RequestRouterImplementation(Map<String, Controller> controllers) {
        this.controllers = controllers;

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream keyStoreStream = getClass().getResourceAsStream(KEYSTORE_PATH)) {
                if (keyStoreStream == null) {
                    throw new FileNotFoundException("Keystore file not found in classpath: " + KEYSTORE_PATH);
                }
                keyStore.load(keyStoreStream, KEYSTORE_PASSWORD.toCharArray());
            }

            serverPrivateKey = (PrivateKey) keyStore.getKey(KEY_ALIAS, KEYSTORE_PASSWORD.toCharArray());

            if (serverPrivateKey == null) {
                throw new RuntimeException("Failed to load private key from keystore.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading server keystore: " + e.getMessage(), e);
        }
    }

    @Override
    public RawHttpResponse<?> execRequest(RawHttpRequest request) {
        String path = request.getUri().getPath();
        String[] pathParts = path.split("/");
        String method = request.getMethod();
        String controllerName;
        String responseJsonBody = "";

        try {
            controllerName = pathParts[1];

            // Extract encrypted symmetric key
            String encryptedSymmetricKeyBase64 = request.getHeaders().getFirst("Symmetric-Key")
                    .orElseThrow(() -> new ServerErrorException("Missing Symmetric-Key header"));

            // Decrypt symmetric key
            String symmetricKeyBase64 = CryptoUtils.asymmetricDecrypt(encryptedSymmetricKeyBase64, serverPrivateKey);
            SecretKey symmetricKey = CryptoUtils.decodeSecretKey(symmetricKeyBase64);

            // Validate and decrypt the request body
            String requestBody = request.getBody().map(body -> {
                try {
                    String encryptedBody = new String(body.asRawBytes(), StandardCharsets.UTF_8);

                    // Validate hash
                    String bodyHash = request.getHeaders().getFirst("Body-Hash")
                            .orElseThrow(() -> new ServerErrorException("Missing Body-Hash header"));
                    String calculatedHash = CryptoUtils.getHash(encryptedBody);
                    if (!calculatedHash.equals(bodyHash)) {
                        throw new ServerErrorException("Body hash mismatch");
                    }

                    // Decrypt body if not empty
                    return encryptedBody.isEmpty() ? "" : CryptoUtils.decrypt(encryptedBody, symmetricKey);
                } catch (Exception e) {
                    throw new ServerErrorException("Error decrypting request body", e);
                }
            }).orElse("");

            switch (controllerName) {
                case "author":
                    responseJsonBody = manageAuthors(request, method, pathParts, requestBody);
                    break;
                case "book":
                    responseJsonBody = manageBooks(request, method, pathParts, requestBody);
                    break;
                case "bookDetail":
                    responseJsonBody = manageBookDetails(request, method, pathParts, requestBody);
                    break;
                case "customer":
                    responseJsonBody = manageCustomers(request, method, pathParts, requestBody);
                    break;
                case "genre":
                    responseJsonBody = manageGenres(request, method, pathParts, requestBody);
                    break;
                case "loan":
                    responseJsonBody = manageLoans(request, method, pathParts, requestBody);
                    break;
                default:
                    throw new ResourceNotFoundException("Unknown controller: " + controllerName);
            }

            // Encrypt response body
            String encryptedResponseBody = CryptoUtils.encrypt(responseJsonBody, symmetricKey);

            return rawHttp.parseResponse("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + encryptedResponseBody.length() + "\r\n" +
                    "\r\n" +
                    encryptedResponseBody);

        } catch (ResourceNotFoundException e) {
            return rawHttp.parseResponse("HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: 0\r\n" +
                    "\r\n");
        } catch (ServerErrorException e) {
            return rawHttp.parseResponse("HTTP/1.1 500 Internal Server Error\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: 0\r\n" +
                    "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
            return rawHttp.parseResponse("HTTP/1.1 500 Internal Server Error\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: 0\r\n" +
                    "\r\n");
        }
    }

    private String manageAuthors(RawHttpRequest request, String method, String[] pathParts, String requestBody) {
        var controller = controllers.get("author");

        if (method.equals("POST")) {
            controller.post(requestBody);
        } else if (method.equals("GET")) {
            if (pathParts.length == 2) {
                return controller.get();
            } else if (pathParts.length == 3) {
                return controller.get(Integer.parseInt(pathParts[2]));
            }
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            controller.delete(Integer.parseInt(pathParts[2]));
        } else if (method.equals("PUT") && pathParts.length == 3) {
            controller.put(Integer.parseInt(pathParts[2]), requestBody);
        }
        return "";
    }

    private String manageBooks(RawHttpRequest request, String method, String[] pathParts, String requestBody) {
        var controller = controllers.get("book");

        if (method.equals("POST")) {
            controller.post(requestBody);
        } else if (method.equals("GET")) {
            if (pathParts.length == 2) {
                return controller.get();
            } else if (pathParts.length == 3) {
                return controller.get(Integer.parseInt(pathParts[2]));
            }
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            controller.delete(Integer.parseInt(pathParts[2]));
        } else if (method.equals("PUT") && pathParts.length == 3) {
            controller.put(Integer.parseInt(pathParts[2]), requestBody);
        }
        return "";
    }

    private String manageBookDetails(RawHttpRequest request, String method, String[] pathParts, String requestBody) {
        var controller = controllers.get("bookDetail");

        if (method.equals("POST")) {
            controller.post(requestBody);
        } else if (method.equals("GET")) {
            if (pathParts.length == 2) {
                return controller.get();
            } else if (pathParts.length == 3) {
                return controller.get(Integer.parseInt(pathParts[2]));
            }
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            controller.delete(Integer.parseInt(pathParts[2]));
        } else if (method.equals("PUT") && pathParts.length == 3) {
            controller.put(Integer.parseInt(pathParts[2]), requestBody);
        }
        return "";
    }

    private String manageCustomers(RawHttpRequest request, String method, String[] pathParts, String requestBody) {
        var controller = controllers.get("customer");

        if (method.equals("POST")) {
            controller.post(requestBody);
        } else if (method.equals("GET")) {
            if (pathParts.length == 2) {
                return controller.get();
            } else if (pathParts.length == 3) {
                return controller.get(Integer.parseInt(pathParts[2]));
            }
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            controller.delete(Integer.parseInt(pathParts[2]));
        } else if (method.equals("PUT") && pathParts.length == 3) {
            controller.put(Integer.parseInt(pathParts[2]), requestBody);
        }
        return "";
    }

    private String manageGenres(RawHttpRequest request, String method, String[] pathParts, String requestBody) {
        var controller = controllers.get("genre");

        if (method.equals("POST")) {
            controller.post(requestBody);
        } else if (method.equals("GET")) {
            if (pathParts.length == 2) {
                return controller.get();
            } else if (pathParts.length == 3) {
                return controller.get(Integer.parseInt(pathParts[2]));
            }
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            controller.delete(Integer.parseInt(pathParts[2]));
        } else if (method.equals("PUT") && pathParts.length == 3) {
            controller.put(Integer.parseInt(pathParts[2]), requestBody);
        }
        return "";
    }

    private String manageLoans(RawHttpRequest request, String method, String[] pathParts, String requestBody) {
        var controller = controllers.get("loan");

        if (method.equals("POST")) {
            controller.post(requestBody);
        } else if (method.equals("GET")) {
            if (pathParts.length == 2) {
                return controller.get();
            } else if (pathParts.length == 3) {
                return controller.get(Integer.parseInt(pathParts[2]));
            }
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            controller.delete(Integer.parseInt(pathParts[2]));
        } else if (method.equals("PUT") && pathParts.length == 3) {
            controller.put(Integer.parseInt(pathParts[2]), requestBody);
        }
        return "";
    }
}
