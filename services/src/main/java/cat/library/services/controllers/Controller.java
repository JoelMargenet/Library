package cat.library.services.controllers;

// Generic interface for a controller handling CRUD operations
public interface Controller {
    String get(int id);  // Get a single resource by key
    String get();       // Get all resources
    String post(String json); // Create a new resource
    String put(int id, String json); // Update an existing resource
    String delete(int id); // Delete a resource by key
}
