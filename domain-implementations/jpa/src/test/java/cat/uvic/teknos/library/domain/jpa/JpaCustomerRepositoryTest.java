package cat.uvic.teknos.library.domain.jpa;

import cat.uvic.teknos.library.domain.jpa.resources.models.Customer;
import cat.uvic.teknos.library.domain.jpa.resources.repositories.JpaCustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JpaCustomerRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private JpaCustomerRepository repository;

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
        repository = new JpaCustomerRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
    }

    @Test
    void saveAndRetrieveCustomer() {
        cat.uvic.teknos.library.models.Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");

        repository.save(customer);

        cat.uvic.teknos.library.models.Customer retrievedCustomer = repository.get(customer.getId());
        assertNotNull(retrievedCustomer);
        assertEquals("John", retrievedCustomer.getFirstName());
    }

    @Test
    void deleteCustomer() {
        cat.uvic.teknos.library.models.Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Doe");

        repository.save(customer);
        repository.delete(customer);

        cat.uvic.teknos.library.models.Customer retrievedCustomer = repository.get(customer.getId());
        assertNull(retrievedCustomer);
    }

    @Test
    void getAllCustomers() {
        cat.uvic.teknos.library.models.Customer customer1 = new Customer();
        customer1.setFirstName("Alice");
        customer1.setLastName("Smith");

        cat.uvic.teknos.library.models.Customer customer2 = new Customer();
        customer2.setFirstName("Bob");
        customer2.setLastName("Brown");

        repository.save(customer1);
        repository.save(customer2);

        Set<cat.uvic.teknos.library.models.Customer> customers = repository.getAll();
        assertTrue(customers.contains(customer1));
        assertTrue(customers.contains(customer2));
    }
}