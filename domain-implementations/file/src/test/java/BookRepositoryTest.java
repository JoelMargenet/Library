import cat.uvic.teknos.library.file.models.Author;
import cat.uvic.teknos.library.file.models.Book;
import cat.uvic.teknos.library.file.models.Genre;
import org.junit.jupiter.api.Test;
import cat.uvic.teknos.library.file.repositories.BookRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest {

    private final Author author = new Author();
    private final Genre genre = new Genre();

    public BookRepositoryTest() {
        author.setId(1);
        author.setFirstName("John");
        author.setLastName("Doe");

        genre.setId(1);
        genre.setName("Fiction");
    }

    @Test
    void save() {
        var repository = new BookRepository();
        var book = new Book();
        book.setId(1);
        book.setTitle("Sample Book");
        book.setAuthor((cat.uvic.teknos.library.models.Author) author);
        book.setGenres((Set<cat.uvic.teknos.library.models.Genre>) genre);
        repository.save(book);
        assertTrue(book.getId() > 0);
        assertNotNull(repository.get(book.getId()));
    }

    @Test
    void update() {
        var repository = new BookRepository();
        var book = new Book();
        book.setId(1);
        book.setTitle("Sample Book");
        book.setAuthor((cat.uvic.teknos.library.models.Author) author);
        book.setGenres((Set<cat.uvic.teknos.library.models.Genre>) genre);
        repository.save(book);

        var updatedBook = repository.get(1);
        updatedBook.setTitle("Updated Book");
        repository.save(updatedBook);

        var retrievedBook = repository.get(1);
        assertEquals("Updated Book", retrievedBook.getTitle());
    }

    @Test
    void delete() {
        var repository = new BookRepository();
        var book = new Book();
        repository.delete(book);
        assertNull(repository.get(book.getId()));
    }

    @Test
    void get() {
        var repository = new BookRepository();
        var book = new Book();
        book.setId(1);
        book.setTitle("Sample Book");
        book.setAuthor(author);
        book.setGenres((Set<cat.uvic.teknos.library.models.Genre>) genre);
        repository.save(book);

        var retrievedBook = repository.get(1);
        assertNotNull(retrievedBook);
        assertEquals("Sample Book", retrievedBook.getTitle());
    }

    @Test
    void getAll() {
        var repository = new BookRepository();
        var book1 = new Book();
        book1.setId(1);
        book1.setTitle("Book 1");
        book1.setAuthor(author);
        book1.setGenres((Set<cat.uvic.teknos.library.models.Genre>) genre);
        repository.save(book1);

        var book2 = new Book();
        book2.setId(2);
        book2.setTitle("Book 2");
        book2.setAuthor(author);
        book2.setGenres((Set<cat.uvic.teknos.library.models.Genre>) genre);
        repository.save(book2);

        var allBooks = repository.getAll();
        assertEquals(2, allBooks.size());
    }
}