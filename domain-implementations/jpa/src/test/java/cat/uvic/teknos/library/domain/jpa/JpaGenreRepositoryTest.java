package cat.uvic.teknos.library.domain.jpa;

import cat.uvic.teknos.library.domain.jpa.resources.models.Genre;
import cat.uvic.teknos.library.domain.jpa.resources.repositories.JpaGenreRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

        import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JpaGenreRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private JpaGenreRepository repository;

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
        repository = new JpaGenreRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
    }

    @Test
    void saveAndRetrieveGenre() {
        cat.uvic.teknos.library.models.Genre genre = new Genre();
        genre.setName("Fiction");

        repository.save(genre);

        cat.uvic.teknos.library.models.Genre retrievedGenre = repository.get(genre.getId());
        assertNotNull(retrievedGenre);
        assertEquals("Fiction", retrievedGenre.getName());
    }

    @Test
    void deleteGenre() {
        cat.uvic.teknos.library.models.Genre genre = new Genre();
        genre.setName("Non-Fiction");

        repository.save(genre);
        repository.delete(genre);

        cat.uvic.teknos.library.models.Genre retrievedGenre = repository.get(genre.getId());
        assertNull(retrievedGenre);
    }

    @Test
    void getAllGenres() {
        cat.uvic.teknos.library.models.Genre genre1 = new Genre();
        genre1.setName("Science Fiction");

        cat.uvic.teknos.library.models.Genre genre2 = new Genre();
        genre2.setName("Fantasy");

        repository.save(genre1);
        repository.save(genre2);

        Set<cat.uvic.teknos.library.models.Genre> genres = repository.getAll();
        assertTrue(genres.contains(genre1));
        assertTrue(genres.contains(genre2));
    }
}