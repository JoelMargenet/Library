package cat.uvic.teknos.library.domain.jpa;

import cat.uvic.teknos.library.domain.jpa.resources.models.Book;
import cat.uvic.teknos.library.domain.jpa.repositories.*;
import cat.uvic.teknos.library.domain.jpa.resources.repositories.JpaBookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JpaBookRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private JpaBookRepository repository;

    @BeforeAll
    static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("library");
    }

    @AfterAll
    static void tearDownClass() {
        entityManagerFactory.close();
    }

    @BeforeEach
    void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        repository = new JpaBookRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
    }

    @Test
    void saveAndRetrieveBook() {
        cat.uvic.teknos.library.models.Book book = new Book();
        book.setTitle("Sample Book");

        repository.save(book);

        cat.uvic.teknos.library.models.Book retrievedBook = repository.get(book.getId());
        assertNotNull(retrievedBook);
        assertEquals("Sample Book", retrievedBook.getTitle());
    }

    @Test
    void deleteBook() {
        cat.uvic.teknos.library.models.Book book = new Book();
        book.setTitle("Sample Book");

        repository.save(book);
        repository.delete(book);

        cat.uvic.teknos.library.models.Book retrievedBook = repository.get(book.getId());
        assertNull(retrievedBook);
    }

    @Test
    void getAllBooks() {
        cat.uvic.teknos.library.models.Book book1 = new Book();
        book1.setTitle("Book 1");

        cat.uvic.teknos.library.models.Book book2 = new Book();
        book2.setTitle("Book 2");

        repository.save(book1);
        repository.save(book2);

        Set<cat.uvic.teknos.library.models.Book> books = repository.getAll();
        assertTrue(books.contains(book1));
        assertTrue(books.contains(book2));
    }
}