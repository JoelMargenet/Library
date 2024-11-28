package cat.uvic.teknos.library.domain.jpa;

import cat.uvic.teknos.library.domain.jpa.resources.models.BookDetail;
import cat.uvic.teknos.library.domain.jpa.resources.repositories.JpaBookDetailRepository;
import cat.uvic.teknos.library.domain.jpa.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JpaBookDetailRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private JpaBookDetailRepository repository;

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
        repository = new JpaBookDetailRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
    }

    @Test
    void saveAndRetrieveBookDetail() {
        cat.uvic.teknos.library.models.BookDetail bookDetail = new BookDetail();
        bookDetail.setDescription("Sample Description");

        repository.save(bookDetail);

        cat.uvic.teknos.library.models.BookDetail retrievedBookDetail = repository.get(bookDetail.getId());
        assertNotNull(retrievedBookDetail);
        assertEquals("Sample Description", retrievedBookDetail.getDescription());
    }

    @Test
    void deleteBookDetail() {
        cat.uvic.teknos.library.models.BookDetail bookDetail = new BookDetail();
        bookDetail.setDescription("Sample Description");

        repository.save(bookDetail);
        repository.delete(bookDetail);

        cat.uvic.teknos.library.models.BookDetail retrievedBookDetail = repository.get(bookDetail.getId());
        assertNull(retrievedBookDetail);
    }

    @Test
    void getAllBookDetails() {
        cat.uvic.teknos.library.models.BookDetail bookDetail1 = new BookDetail();
        bookDetail1.setDescription("Description 1");

        cat.uvic.teknos.library.models.BookDetail bookDetail2 = new BookDetail();
        bookDetail2.setDescription("Description 2");

        repository.save(bookDetail1);
        repository.save(bookDetail2);

        Set<cat.uvic.teknos.library.models.BookDetail> bookDetails = repository.getAll();
        assertTrue(bookDetails.contains(bookDetail1));
        assertTrue(bookDetails.contains(bookDetail2));
    }
}