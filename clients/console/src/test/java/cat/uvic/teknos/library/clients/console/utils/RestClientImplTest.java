package cat.uvic.teknos.library.clients.console.utils;

import cat.uvic.teknos.library.clients.console.dto.AuthorDto;
import cat.uvic.teknos.library.clients.console.dto.BookDto;
import cat.uvic.teknos.library.clients.console.dto.CustomerDto;
import cat.uvic.teknos.library.clients.console.dto.GenreDto;
import cat.uvic.teknos.library.clients.console.dto.LoanDto;
import cat.uvic.teknos.library.clients.console.exceptions.RequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RestClientImplTest {

    @Test
    void getBookTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            BookDto book = restClient.get("books/1", BookDto.class);
            assertNotNull(book);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllBooksTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            BookDto[] books = restClient.getAll("books", BookDto[].class);
            assertNotNull(books);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postBookTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            var book = new BookDto();
            book.setTitle("Test Book");
            book.setISBN("123-456-789");

            restClient.post("books", Mappers.get().writeValueAsString(book));
        } catch (RequestException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAuthorTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            AuthorDto author = restClient.get("authors/1", AuthorDto.class);
            assertNotNull(author);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllAuthorsTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            AuthorDto[] authors = restClient.getAll("authors", AuthorDto[].class);
            assertNotNull(authors);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postAuthorTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            var author = new AuthorDto();
            author.setFirstName("John");
            author.setLastName("Doe");

            restClient.post("authors", Mappers.get().writeValueAsString(author));
        } catch (RequestException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getCustomerTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            CustomerDto customer = restClient.get("customers/1", CustomerDto.class);
            assertNotNull(customer);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllCustomersTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            CustomerDto[] customers = restClient.getAll("customers", CustomerDto[].class);
            assertNotNull(customers);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postCustomerTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            var customer = new CustomerDto();
            customer.setFirstName("Alice");
            customer.setLastName("Smith");
            customer.setEmail("alice.smith@example.com");
            customer.setAddress("123 Main St.");
            customer.setPhoneNumber("555-1234");

            restClient.post("customers", Mappers.get().writeValueAsString(customer));
        } catch (RequestException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getGenreTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            GenreDto genre = restClient.get("genres/1", GenreDto.class);
            assertNotNull(genre);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllGenresTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            GenreDto[] genres = restClient.getAll("genres", GenreDto[].class);
            assertNotNull(genres);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postGenreTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            var genre = new GenreDto();
            genre.setName("Fantasy");

            restClient.post("genres", Mappers.get().writeValueAsString(genre));
        } catch (RequestException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getLoanTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            LoanDto loan = restClient.get("loans/1", LoanDto.class);
            assertNotNull(loan);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllLoansTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            LoanDto[] loans = restClient.getAll("loans", LoanDto[].class);
            assertNotNull(loans);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postLoanTest() {
        var restClient = new RestClientImpl("localhost", 8080);
        try {
            var loan = new LoanDto();
            loan.setLoanDate(java.sql.Date.valueOf("2024-11-20"));
            loan.setReturnDate(java.sql.Date.valueOf("2024-12-20"));

            restClient.post("loans", Mappers.get().writeValueAsString(loan));
        } catch (RequestException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
