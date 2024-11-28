package cat.library.services.controllers;

import cat.library.services.utils.Mappers;
import cat.uvic.teknos.library.domain.jdbc.models.Book;
import cat.uvic.teknos.library.repositories.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookController implements Controller {
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper = Mappers.get();

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String get() {
        try {
            var books = bookRepository.getAll();
            return objectMapper.writeValueAsString(books);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // GET a single Book by ID
    public String get(int id) {
        var book = bookRepository.get(id);
        if (book == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(book);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }


    public String post(String bookJson) {
        try {
            Book book = objectMapper.readValue(bookJson, Book.class);
            bookRepository.save(book);
            return "{\"message\": \"Book created successfully\", \"id\": " + book.getId() + "}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while creating book\"}";
        }
    }


    public String put(int id, String bookJson) {
        try {
            Book updatedBook = objectMapper.readValue(bookJson, Book.class);
            updatedBook.setId(id);

            var existingBook = bookRepository.get(id);
            if (existingBook != null) {
                existingBook.setTitle(updatedBook.getTitle());
                existingBook.setAuthor(updatedBook.getAuthor());
                existingBook.setPublicationDate(updatedBook.getPublicationDate());
                existingBook.setISBN(updatedBook.getISBN());
                existingBook.setCopies(updatedBook.getCopies());

                bookRepository.save(existingBook);
                return "{\"message\": \"Book updated successfully\"}";
            } else {
                return "{\"error\": \"Book not found\"}";
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while updating book\"}";
        }
    }

    public String delete(int id) {
        var book = bookRepository.get(id);
        if (book == null) {
            return "{\"error\": \"Book not found\"}";
        }
        try {
            bookRepository.delete(book);
            return "{\"message\": \"Book deleted successfully\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while deleting book\"}";
        }
    }
}