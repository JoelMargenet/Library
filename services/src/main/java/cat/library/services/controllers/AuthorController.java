package cat.library.services.controllers;

import cat.library.services.utils.Mappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cat.uvic.teknos.library.domain.jdbc.models.Author;
import cat.uvic.teknos.library.repositories.AuthorRepository;

public class AuthorController implements Controller {
    private final AuthorRepository authorRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public String get(int id) {
        var author = authorRepository.get(id);
        if (author == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(author);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public String get() {
        try {
            return objectMapper.writeValueAsString(authorRepository.getAll());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public String post(String authorJson) {
        try {
            Author author = objectMapper.readValue(authorJson, Author.class);
            authorRepository.save(author);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public String put(int id, String authorJson) {
        System.out.println("Attempting to update author with ID: " + id);
        try {
            var updatedAuthor = Mappers.get().readValue(authorJson, Author.class);
            updatedAuthor.setId(id);

            var existingAuthor = authorRepository.get(id);
            if (existingAuthor != null) {
                existingAuthor.setFirstName(updatedAuthor.getFirstName());
                existingAuthor.setLastName(updatedAuthor.getLastName());

                authorRepository.save(existingAuthor);
                return  ("Successfully updated author: " + existingAuthor);
            } else {
                return  ("No author found with ID: " + id);
            }
        } catch (JsonProcessingException e) {
            return ("Failed to parse JSON: " + e.getMessage());
        } catch (Exception e) {
            return ("An unexpected error occurred: " + e.getMessage());
        }
    }

    public String delete(int id) {
        try {

            var author = authorRepository.get(id);
            if (author != null) {

                System.out.println("Deleting author: " + author);
                authorRepository.delete(author);
                return "{}";
            } else {
                // Author not found, return empty JSON
                System.out.println("Author not found with ID: " + id);
                return "{}"; // Return empty JSON for not found
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log any exception for debugging
            return "{ \"error\": \"" + e.getMessage() + "\" }"; // Return error message
        }
    }
}