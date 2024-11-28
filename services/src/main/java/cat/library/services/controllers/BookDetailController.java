package cat.library.services.controllers;

import cat.library.services.utils.Mappers;
import cat.uvic.teknos.library.domain.jdbc.models.BookDetail;
import cat.uvic.teknos.library.repositories.BookDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookDetailController implements Controller {
    private final BookDetailRepository bookDetailRepository;
    private final ObjectMapper objectMapper = Mappers.get();

    public BookDetailController(BookDetailRepository bookDetailRepository) {
        this.bookDetailRepository = bookDetailRepository;
    }

    public String get() {
        try {
            var bookDetails = bookDetailRepository.getAll();
            return objectMapper.writeValueAsString(bookDetails);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public String get(int id) {
        var bookDetail = bookDetailRepository.get(id);
        if (bookDetail == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(bookDetail);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public String post(String bookDetailJson) {
        try {
            BookDetail bookDetail = objectMapper.readValue(bookDetailJson, BookDetail.class);
            bookDetailRepository.save(bookDetail);
            return "{\"message\": \"Book detail created successfully\", \"id\": " + bookDetail.getId() + "}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while creating book detail\"}";
        }
    }

    public String put(int id, String bookDetailJson) {
        try {
            var updatedBookDetail = objectMapper.readValue(bookDetailJson, BookDetail.class);
            updatedBookDetail.setId(id);

            var existingBookDetail = bookDetailRepository.get(id);
            if (existingBookDetail != null) {
                existingBookDetail.setDescription(updatedBookDetail.getDescription());
                existingBookDetail.setReviews(updatedBookDetail.getReviews());

                if (updatedBookDetail.getBook() != null) {
                    existingBookDetail.setBook(updatedBookDetail.getBook());
                }

                bookDetailRepository.save(existingBookDetail);

            } else {
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ("{ \"error\": \"Invalid JSON format.\" }");
        } catch (Exception e) {
            e.printStackTrace();
            return ("{ \"error\": \"Error updating BookDetail.\" }");
        }
        return "{}";
    }

    public String delete(int id) {
        var bookDetail = bookDetailRepository.get(id);
        if (bookDetail == null) {
            return "{ \"error\": \"BookDetail not found.\" }";
        }
        try {
            bookDetailRepository.delete(bookDetail);
            return "{ \"message\": \"BookDetail deleted successfully.\" }";
        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"Error deleting BookDetail.\" }";
        }
    }
}