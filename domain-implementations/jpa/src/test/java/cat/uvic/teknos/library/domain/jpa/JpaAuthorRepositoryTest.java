package cat.uvic.teknos.library.domain.jpa;


import cat.uvic.teknos.library.domain.jpa.resources.models.Author;
import cat.uvic.teknos.library.domain.jpa.repositories.*;

import cat.uvic.teknos.library.domain.jpa.resources.repositories.JpaAuthorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JpaAuthorRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private JpaAuthorRepository repository;

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
        repository = new JpaAuthorRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
    }

    @Test
    void saveAndRetrieveAuthor() {
        cat.uvic.teknos.library.models.Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Doe");

        repository.save(author);

        cat.uvic.teknos.library.models.Author retrievedAuthor = repository.get(author.getId());
        assertNotNull(retrievedAuthor);
        assertEquals("John", retrievedAuthor.getFirstName());
    }

    @Test
    void deleteAuthor() {
        cat.uvic.teknos.library.models.Author author = new Author();
        author.setFirstName("Jane");
        author.setLastName("Doe");

        repository.save(author);
        repository.delete(author);

        cat.uvic.teknos.library.models.Author retrievedAuthor = repository.get(author.getId());
        assertNull(retrievedAuthor);
    }

    @Test
    void getAllAuthors() {
        cat.uvic.teknos.library.models.Author author1 = new Author();
        author1.setFirstName("Alice");
        author1.setLastName("Smith");

        cat.uvic.teknos.library.models.Author author2 = new Author();
        author2.setFirstName("Bob");
        author2.setLastName("Brown");

        repository.save(author1);
        repository.save(author2);

        Set<cat.uvic.teknos.library.models.Author> authors = repository.getAll();
        assertTrue(authors.contains(author1));
        assertTrue(authors.contains(author2));
    }
}