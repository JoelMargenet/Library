package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.domain.jdbc.models.Customer;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcCustomerRepositoryTest {

    private final Connection connection;

    public JdbcCustomerRepositoryTest(Connection connection){
        this.connection = connection;
    }


    @Test
    void shouldInsertCustomer() {

        Customer customer = new Customer();
        customer.setFirstName("Ivan");
        customer.setLastName("Vidal");
        customer.setEmail("Ivan.Vidal@uvic.cat");
        customer.setAddress("Tona");
        customer.setPhoneNumber("666400762");


        var repository = new JdbcCustomerRepository(connection);
        repository.save(customer);

        assertTrue(customer.getId() > 0);


        DbAssertions.assertThat(connection)
                .table("CUSTOMER")
                .where("CUSTOMER_ID", customer.getId())
                .hasOneLine();

    }

    @Test
    void shouldUpdateCustomer(){

        Customer customer = new Customer();
        customer.setId(2);
        customer.setFirstName("Ivan");
        customer.setLastName("Vidal");
        customer.setEmail("Vidal.Ivan@uvic.cat");
        customer.setAddress("Tona");
        customer.setPhoneNumber("666400762");



        var repository = new JdbcCustomerRepository(connection);
        repository.save(customer);


    }

    @Test
    void delete() {
        Customer customer = new Customer();
        customer.setId(2);

        var repository = new JdbcCustomerRepository(connection);

        repository.save(customer);

        repository.delete(customer);

        assertNull(repository.get(customer.getId()));
        assertTrue(repository.getAllLoansByCustomerId(customer.getId()).isEmpty());
    }

    @Test
    void get() {
        int id = 2;

        var repository = new JdbcCustomerRepository(connection);
        Customer customer = (Customer) repository.get(id);
        SoutCustomer(customer);
    }

    @Test
    void getAll() {
        var repository = new JdbcCustomerRepository(connection);
        Set<cat.uvic.teknos.library.models.Customer> customers = repository.getAll();

        for(var customer:customers){
            SoutCustomer(customer);
        }
    }

    private void SoutCustomer(cat.uvic.teknos.library.models.Customer customer){
        System.out.println("Customer: " + customer.getId());
        System.out.println("Name: " + customer.getFirstName());
        System.out.println("Last Name: " + customer.getLastName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Address: " + customer.getAddress());



        System.out.println("\n\n");
    }

}