package cat.library.services;

import cat.library.services.controllers.Controller;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.util.Map;

public class RequestRouterImplementation implements RequestRouter {
    private static final RawHttp rawHttp = new RawHttp();
    private final Map<String, Controller> controllers;

    public RequestRouterImplementation(Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    @Override
    public RawHttpResponse<?> execRequest(RawHttpRequest request) {
        var path = request.getUri().getPath();
        var method = request.getMethod();
        var pathParts = path.split("/");

        // Log incoming requests for debugging
        System.out.println("Received request for path: " + path);
        System.out.println("Method: " + method);
        System.out.println("Path Parts: " + String.join(", ", pathParts));

        String responseJsonBody = "";

        // Ensure proper indexing for the pathParts array
        if (pathParts.length < 2) {
            return rawHttp.parseResponse("HTTP/1.1 404 Not Found\r\n\r\n");
        }

        switch (pathParts[1]) {
            case "author":
                responseJsonBody = manageAuthors(request, method, pathParts);
                break;
            case "book": // Correctly match "book" instead of "books"
                responseJsonBody = manageBooks(request, method, pathParts);
                break;
            case "bookDetail":
                responseJsonBody = manageBookDetails(request, method, pathParts);
                break;
            case "customer":
                responseJsonBody = manageCustomers(request, method, pathParts);
                break;
            case "genre":
                responseJsonBody = manageGenres(request, method, pathParts);
                break;
            case "loan":
                responseJsonBody = manageLoans(request, method, pathParts);
                break;
            default:
                return rawHttp.parseResponse("HTTP/1.1 404 Not Found\r\n\r\n");
        }

        // Create HTTP response
        try {
            return rawHttp.parseResponse("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + responseJsonBody.length() + "\r\n" +
                    "\r\n" +
                    responseJsonBody);
        } catch (Exception e) {
            return rawHttp.parseResponse("HTTP/1.1 500 Internal Server Error\r\n\r\n");
        }
    }

    private String manageAuthors(RawHttpRequest request, String method, String[] pathParts) {
        var controller = controllers.get("author");

        if (method.equals("GET")) {
            if (pathParts.length == 3) { // Single author by ID
                try {
                    int authorId = Integer.parseInt(pathParts[2]);
                    return controller.get(authorId);
                } catch (NumberFormatException e) {
                    return ""; // Handle invalid ID format
                }
            } else { // All authors
                return controller.get();
            }
        } else if (method.equals("POST")) {
            var authorJson = request.getBody().get().toString();
            controller.post(authorJson);
            return ""; // Return empty response for POST
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            try {
                var authorId = Integer.parseInt(pathParts[2]);
                return controller.delete(authorId);
            } catch (NumberFormatException e) {
                return ""; // Handle invalid ID format
            }
        } else if (method.equals("PUT") && pathParts.length == 3) {
            var authorId = Integer.parseInt(pathParts[2]);
            var authorJson = request.getBody().get().toString();
            controller.put(authorId, authorJson);
            return ""; // Return empty response for PUT
        }

        return ""; // Default return if nothing matches
    }

    private String manageBooks(RawHttpRequest request, String method, String[] pathParts) {
        var controller = controllers.get("book");

        if (method.equals("GET")) {
            if (pathParts.length == 3) { // Single book by ID
                try {
                    int bookId = Integer.parseInt(pathParts[2]);
                    return controller.get(bookId);
                } catch (NumberFormatException e) {
                    return ""; // Handle invalid ID format
                }
            } else { // All books
                return controller.get();
            }
        } else if (method.equals("POST")) {
            var bookJson = request.getBody().get().toString();
            controller.post(bookJson);
            return ""; // Return empty response for POST
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            try {
                var bookId = Integer.parseInt(pathParts[2]);
                return controller.delete(bookId);
            } catch (NumberFormatException e) {
                return ""; // Handle invalid ID format
            }
        } else if (method.equals("PUT") && pathParts.length == 3) {
            var bookId = Integer.parseInt(pathParts[2]);
            var bookJson = request.getBody().get().toString();
            controller.put(bookId, bookJson);
            return ""; // Return empty response for PUT
        }

        return ""; // Default return if nothing matches
    }

    private String manageBookDetails(RawHttpRequest request, String method, String[] pathParts) {
        var controller = controllers.get("bookDetail");

        if (method.equals("GET")) {
            if (pathParts.length == 3) { // Single book detail by ID
                try {
                    int bookDetailId = Integer.parseInt(pathParts[2]);
                    return controller.get(bookDetailId);
                } catch (NumberFormatException e) {
                    return ""; // Handle invalid ID format
                }
            } else { // All book details
                return controller.get();
            }
        } else if (method.equals("POST")) {
            var bookDetailJson = request.getBody().get().toString();
            controller.post(bookDetailJson);
            return ""; // Return empty response for POST
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            try {
                var bookDetailId = Integer.parseInt(pathParts[2]);
                return controller.delete(bookDetailId);
            } catch (NumberFormatException e) {
                return ""; // Handle invalid ID format
            }
        } else if (method.equals("PUT") && pathParts.length == 3) {
            var bookDetailId = Integer.parseInt(pathParts[2]);
            var bookDetailJson = request.getBody().get().toString();
            controller.put(bookDetailId, bookDetailJson);
            return ""; // Return empty response for PUT
        }

        return ""; // Default return if nothing matches
    }

    private String manageCustomers(RawHttpRequest request, String method, String[] pathParts) {
        var controller = controllers.get("customer");

        if (method.equals("GET")) {
            if (pathParts.length == 3) { // Single customer by ID
                try {
                    int customerId = Integer.parseInt(pathParts[2]);
                    return controller.get(customerId);
                } catch (NumberFormatException e) {
                    return ""; // Handle invalid ID format
                }
            } else { // All customers
                return controller.get();
            }
        } else if (method.equals("POST")) {
            var customerJson = request.getBody().get().toString();
            controller.post(customerJson);
            return ""; // Return empty response for POST
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            try {
                var customerId = Integer.parseInt(pathParts[2]);
                return controller.delete(customerId);
            } catch (NumberFormatException e) {
                return ""; // Handle invalid ID format
            }
        } else if (method.equals("PUT") && pathParts.length == 3) {
            var customerId = Integer.parseInt(pathParts[2]);
            var customerJson = request.getBody().get().toString();
            controller.put(customerId, customerJson);
            return ""; // Return empty response for PUT
        }

        return ""; // Default return if nothing matches
    }

    private String manageGenres(RawHttpRequest request, String method, String[] pathParts) {
        var controller = controllers.get("genre");

        if (method.equals("GET")) {
            if (pathParts.length == 3) { // Single genre by ID
                try {
                    int genreId = Integer.parseInt(pathParts[2]);
                    return controller.get(genreId);
                } catch (NumberFormatException e) {
                    return ""; // Handle invalid ID format
                }
            } else { // All genres
                return controller.get();
            }
        } else if (method.equals("POST")) {
            var genreJson = request.getBody().get().toString();
            controller.post(genreJson);
            return ""; // Return empty response for POST
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            try {
                var genreId = Integer.parseInt(pathParts[2]);
                return controller.delete(genreId);
            } catch (NumberFormatException e) {
                return ""; // Handle invalid ID format
            }
        } else if (method.equals("PUT") && pathParts.length == 3) {
            var genreId = Integer.parseInt(pathParts[2]);
            var genreJson = request.getBody().get().toString();
            controller.put(genreId, genreJson);
            return ""; // Return empty response for PUT
        }

        return ""; // Default return if nothing matches
    }

    private String manageLoans(RawHttpRequest request, String method, String[] pathParts) {
        var controller = controllers.get("loan");

        if (method.equals("GET")) {
            if (pathParts.length == 3) { // Single loan by ID
                try {
                    int loanId = Integer.parseInt(pathParts[2]);
                    return controller.get(loanId);
                } catch (NumberFormatException e) {
                    return ""; // Handle invalid ID format
                }
            } else { // All loans
                return controller.get();
            }
        } else if (method.equals("POST")) {
            var loanJson = request.getBody().get().toString();
            controller.post(loanJson);
            return ""; // Return empty response for POST
        } else if (method.equals("DELETE") && pathParts.length == 3) {
            try {
                var loanId = Integer.parseInt(pathParts[2]);
                return controller.delete(loanId);
            } catch (NumberFormatException e) {
                return ""; // Handle invalid ID format
            }
        } else if (method.equals("PUT") && pathParts.length == 3) {
            var loanId = Integer.parseInt(pathParts[2]);
            var loanJson = request.getBody().get().toString();
            controller.put(loanId, loanJson);
            return ""; // Return empty response for PUT
        }

        return ""; // Default return if nothing matches
    }
}