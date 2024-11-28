package cat.uvic.teknos.library.domain.jpa;

import cat.uvic.teknos.library.domain.jpa.resources.models.Loan;
import cat.uvic.teknos.library.domain.jpa.repositories.*;
import cat.uvic.teknos.library.domain.jpa.resources.repositories.JpaLoanRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JpaLoanRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private JpaLoanRepository repository;

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
        repository = new JpaLoanRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
    }

    @Test
    void saveAndRetrieveLoan() {
        cat.uvic.teknos.library.models.Loan loan = new Loan();
        loan.setLoanDate(new java.util.Date());

        repository.save(loan);

        cat.uvic.teknos.library.models.Loan retrievedLoan = repository.get(loan.getId());
        assertNotNull(retrievedLoan);
        assertEquals(loan.getLoanDate(), retrievedLoan.getLoanDate());
    }

    @Test
    void deleteLoan() {
        cat.uvic.teknos.library.models.Loan loan = new Loan();
        loan.setLoanDate(new java.util.Date());

        repository.save(loan);
        repository.delete(loan);

        cat.uvic.teknos.library.models.Loan retrievedLoan = repository.get(loan.getId());
        assertNull(retrievedLoan);
    }

    @Test
    void getAllLoans() {
        cat.uvic.teknos.library.models.Loan loan1 = new Loan();
        loan1.setLoanDate(new java.util.Date());

        cat.uvic.teknos.library.models.Loan loan2 = new Loan();
        loan2.setLoanDate(new java.util.Date());

        repository.save(loan1);
        repository.save(loan2);

        Set<cat.uvic.teknos.library.models.Loan> loans = repository.getAll();
        assertTrue(loans.contains(loan1));
        assertTrue(loans.contains(loan2));
    }
}