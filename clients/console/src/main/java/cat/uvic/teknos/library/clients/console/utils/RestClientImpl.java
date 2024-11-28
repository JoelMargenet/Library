package cat.uvic.teknos.library.clients.console.utils;

import cat.uvic.teknos.library.clients.console.exceptions.ConsoleClientException;
import cat.uvic.teknos.library.clients.console.exceptions.RequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.*;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class RestClientImpl implements RestClient {
    private final int port;
    private final String host;
    private final RawHttp rawHttp = new RawHttp();
    private final ObjectMapper objectMapper = Mappers.get();


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