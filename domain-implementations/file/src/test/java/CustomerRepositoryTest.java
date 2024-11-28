import cat.uvic.teknos.library.file.models.Customer;
import org.junit.jupiter.api.Test;
import cat.uvic.teknos.library.file.repositories.CustomerRepository;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {

    @Test
    void save() {
        var repository = new CustomerRepository();
        var customer = new Customer();
        customer.setId(1);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        repository.save(customer);
        assertTrue(customer.getId() > 0);
        assertNotNull(repository.get(customer.getId()));
    }

    @Test
    void update() {
        var repository = new CustomerRepository();
        var customer = new Customer();
        customer.setId(1);
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        repository.save(customer);

        var updatedCustomer = repository.get(1);
        updatedCustomer.setFirstName("Updated Name");
        repository.save(updatedCustomer);

        var retrievedCustomer = repository.get(1);
        assertEquals("Updated Name", retrievedCustomer.getFirstName());
    }

    @Test
    void delete() {
        var repository = new CustomerRepository();
        var customer = new Customer();
        repository.delete(customer);
        assertNull(repository.get(customer.getId()));
    }

    @Test
    void get() {
        var repository = new CustomerRepository();
        var customer = new Customer();
        customer.setId(1);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        repository.save(customer);

        var retrievedCustomer = repository.get(1);
        assertNotNull(retrievedCustomer);
        assertEquals("John", retrievedCustomer.getFirstName());
        assertEquals("Doe", retrievedCustomer.getLastName());
    }

    @Test
    void getAll() {
        var repository = new CustomerRepository();
        var customer1 = new Customer();
        customer1.setId(1);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        repository.save(customer1);

        var customer2 = new Customer();
        customer2.setId(2);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        repository.save(customer2);

        var customers = repository.getAll();
        assertEquals(2, customers.size());
        assertTrue(customers.contains(customer1));
        assertTrue(customers.contains(customer2));
    }
}