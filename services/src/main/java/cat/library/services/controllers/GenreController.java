package cat.library.services.controllers;

import cat.library.services.utils.Mappers;
import cat.uvic.teknos.library.domain.jdbc.models.Genre;
import cat.uvic.teknos.library.repositories.GenreRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenreController implements Controller {
    private final GenreRepository genreRepository;
    private final ObjectMapper objectMapper = Mappers.get();

    public GenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public String get() {
        try {
            var genres = genreRepository.getAll();
            return objectMapper.writeValueAsString(genres);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // GET a single Genre by ID
    public String get(int id) {
        var genre = genreRepository.get(id);
        if (genre == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(genre);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public String post(String genreJson) {
        try {
            Genre genre = objectMapper.readValue(genreJson, Genre.class);
            genreRepository.save(genre);
            return "{\"message\": \"Genre created successfully\", \"id\": " + genre.getId() + "}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while creating genre: " + e.getMessage() + "\"}";
        }
    }

    public String put(int id, String genreJson) {
        try {
            Genre updatedGenre = objectMapper.readValue(genreJson, Genre.class);
            updatedGenre.setId(id);

            var existingGenre = genreRepository.get(id);
            if (existingGenre != null) {
                existingGenre.setName(updatedGenre.getName());
                genreRepository.save(existingGenre);
                return ("{\"message\": \"Genre updated successfully\"}");
            } else {
                return ("{\"error\": \"Genre not found\"}");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ("{\"error\": \"Invalid JSON format.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ("{\"error\": \"Error updating genre: " + e.getMessage() + "\"}");
        }
    }

    public String delete(int id) {
        var genre = genreRepository.get(id);
        if (genre == null) {
            return "{ \"error\": \"Genre not found.\" }";
        }
        try {
            genreRepository.delete(genre);
            return "{ \"message\": \"Genre deleted successfully.\" }";
        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"Error deleting genre: " + e.getMessage() + "\" }";
        }
    }
}