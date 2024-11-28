package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.domain.jdbc.models.Loan;
import cat.uvic.teknos.library.domain.jdbc.models.Book;
import cat.uvic.teknos.library.domain.jdbc.models.Customer;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcLoanRepositoryTest {

    private final Connection connection;

    public JdbcLoanRepositoryTest(Connection connection) {
        this.connection = connection;
    }

    @Test
    void shouldInsertLoan() {
        Book book = new Book();
        book.setId(1); // Assuming book with ID 1 exists

        Customer customer = new Customer();
        customer.setId(1); // Assuming customer with ID 1 exists

        cat.uvic.teknos.library.models.Loan loan = new Loan();
        loan.setBook(book);
        loan.setCustomer(customer);
        loan.setLoanDate(Date.valueOf("2023-01-01"));
        loan.setReturnDate(Date.valueOf("2023-02-01"));

        var loanRepository = new JdbcLoanRepository(connection);

        loanRepository.save(loan);

        assertNotNull(loan.getId()); // Ensure loan ID is assigned by the repository

        DbAssertions.assertThat(connection)
                .table("LOAN")
                .where("BOOK_ID", loan.getBook().getId())
                .where("CUSTOMER_ID", loan.getCustomer().getId())
                .hasOneLine();
    }

    @Test
    void shouldUpdateLoan() {
        Book book = new Book();
        book.setId(1); // Assuming book with ID 1 exists

        Customer customer = new Customer();
        customer.setId(1); // Assuming customer with ID 1 exists

        cat.uvic.teknos.library.models.Loan loan = new Loan();
        loan.setBook(book);
        loan.setCustomer(customer);
        loan.setLoanDate(Date.valueOf("2023-01-01"));
        loan.setReturnDate(Date.valueOf("2023-02-01"));

        var loanRepository = new JdbcLoanRepository(connection);
        loanRepository.save(loan);

        DbAssertions.assertThat(connection)
                .table("LOAN")
                .where("BOOK_ID", loan.getBook().getId())
                .where("CUSTOMER_ID", loan.getCustomer().getId())
                .hasOneLine();
    }

    @Test
    void delete() {
        cat.uvic.teknos.library.models.Loan loan = new Loan();
        Book book = new Book();
        book.setId(1);
        loan.setBook(book);

        var repository = new JdbcLoanRepository(connection);
        repository.delete(loan);

        assertNull(repository.get(loan.getBook().getId()));
    }

    @Test
    void get() {
        int id = 3; // Assuming loan with ID 3 exists
        var repository = new JdbcLoanRepository(connection);

        cat.uvic.teknos.library.models.Loan loan = repository.get(id);
        printLoan(loan);
    }

    @Test
    void getAll() {
        var loanRepository = new JdbcLoanRepository(connection);

        Set<cat.uvic.teknos.library.models.Loan> loans = loanRepository.getAll();

        assertFalse(loans.isEmpty());
    }

    private void printLoan(cat.uvic.teknos.library.models.Loan loan) {
        System.out.println("Loan id: " + loan.getId());
        System.out.println("Book id: " + loan.getBook().getId());
        System.out.println("Customer id: " + loan.getCustomer().getId());
        System.out.println("Loan date: " + loan.getLoanDate());
        System.out.println("Return date: " + loan.getReturnDate());

        System.out.println("\n\n");
    }
}