import cat.uvic.teknos.library.file.models.Loan;
import org.junit.jupiter.api.Test;
import cat.uvic.teknos.library.file.repositories.LoanRepository;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanRepositoryTest {

    @Test
    void save() {
        var repository = new LoanRepository();
        var loan = new Loan();
        loan.setId(1);
        loan.setLoanDate(Date.valueOf(LocalDate.parse("2024-04-20")));
        loan.setReturnDate(Date.valueOf(LocalDate.parse("2024-05-20")));
        repository.save(loan);
        assertTrue(loan.getId() > 0);
        assertNotNull(repository.get(loan.getId()));
    }

    @Test
    void update() {
        var repository = new LoanRepository();
        var loan = new Loan();
        loan.setId(1);
        loan.setLoanDate(Date.valueOf(LocalDate.parse("2024-04-20")));
        loan.setReturnDate(Date.valueOf(LocalDate.parse("2024-05-20")));
        repository.save(loan);

        var updatedLoan = repository.get(1);
        updatedLoan.setReturnDate(Date.valueOf(LocalDate.parse("2024-05-25")));
        repository.save(updatedLoan);

        var retrievedLoan = repository.get(1);
        assertEquals(LocalDate.parse("2024-05-25"), retrievedLoan.getReturnDate());
    }

    @Test
    void delete() {
        var repository = new LoanRepository();
        var loan = new Loan();
        repository.delete(loan);
        assertNull(repository.get(loan.getId()));
    }

    @Test
    void get() {
        var repository = new LoanRepository();
        var loan = new Loan();
        loan.setId(1);
        loan.setLoanDate(Date.valueOf(LocalDate.parse("2024-04-20")));
        loan.setReturnDate(Date.valueOf(LocalDate.parse("2024-05-20")));
        repository.save(loan);

        var retrievedLoan = repository.get(1);
        assertNotNull(retrievedLoan);
        assertEquals(LocalDate.parse("2024-04-20"), retrievedLoan.getLoanDate());
    }

    @Test
    void getAll() {
        var repository = new LoanRepository();
        var loan1 = new Loan();
        loan1.setId(1);
        loan1.setLoanDate(Date.valueOf(LocalDate.parse("2024-04-20")));
        loan1.setReturnDate(Date.valueOf(LocalDate.parse("2024-05-20")));
        repository.save(loan1);

        var loan2 = new Loan();
        loan2.setId(2);
        loan2.setLoanDate(Date.valueOf(LocalDate.parse("2024-04-21")));
        loan2.setReturnDate(Date.valueOf(LocalDate.parse("2024-05-21")));
        repository.save(loan2);

        var loans = repository.getAll();
        assertEquals(2, loans.size());
        assertTrue(loans.contains(loan1));
        assertTrue(loans.contains(loan2));
    }
}