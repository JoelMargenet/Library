package cat.uvic.teknos.library.backoffice;

import cat.uvic.teknos.library.domain.jdbc.models.Loan;
import cat.uvic.teknos.library.models.Customer;
import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.repositories.LoanRepository;
import cat.uvic.teknos.library.repositories.CustomerRepository;
import cat.uvic.teknos.library.repositories.BookRepository;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

import static cat.uvic.teknos.library.backoffice.IOUtils.readLine;

public class LoansManager {
    private final BufferedReader in;
    private final PrintStream out;
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    public LoansManager(BufferedReader in, PrintStream out, LoanRepository loanRepository,
                        CustomerRepository customerRepository, BookRepository bookRepository) {
        this.in = in;
        this.out = out;
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.bookRepository = bookRepository;
    }


    public void start() {
        out.println("Loans:");
        out.println("1 to create a new Loan");
        out.println("2 to update a Loan");
        out.println("3 to show all Loans");
        out.println("4 to get Loan by Book ID");
        out.println("5 to delete Loan by Book ID");
        out.println("Write exit to go to the main menu \n");

        var command = "";
        do {
            command = readLine(in);

            try {
                switch (command) {
                    case "1" -> createLoan();
                    case "2" -> updateLoan();
                    case "3" -> showAllLoans();
                    case "4" -> getLoanByBookId();
                    case "5" -> deleteLoanByBookId();
                }
            } catch (Exception e) {
                out.println("An error occurred: " + e.getMessage());
                e.printStackTrace(out);
            }

        } while (!command.equals("exit"));

        out.println("Bye!");
    }

    private void createLoan() {
        var loan = new Loan();

        out.println("Enter customer ID:");
        int customerId = Integer.parseInt(readLine(in));
        Customer customer = customerRepository.get(customerId);
        if (customer == null) {
            out.println("Customer not found with ID: " + customerId);
            return;
        }
        loan.setCustomer(customer);

        out.println("Enter book ID:");
        int bookId = Integer.parseInt(readLine(in));
        Book book = bookRepository.get(bookId);
        if (book == null) {
            out.println("Book not found with ID: " + bookId);
            return;
        }
        loan.setBook(book);

        out.println("Enter loan date (yyyy-mm-dd):");
        Date loanDate = Date.valueOf(LocalDate.parse(readLine(in)));
        loan.setLoanDate(loanDate);

        out.println("Enter return date (yyyy-mm-dd):");
        Date returnDate = Date.valueOf(LocalDate.parse(readLine(in)));
        loan.setReturnDate(returnDate);

        loanRepository.save(loan);

        out.println("Created loan successfully: " + loan);

        out.println();
        start();
    }

    private void updateLoan() {
        out.println("Update Loan:");
        out.println("Enter the Book ID of the loan you want to update:");
        int bookId = Integer.parseInt(readLine(in));

        var loan = loanRepository.get(bookId);
        if (loan == null) {
            out.println("Loan not found with Book ID: " + bookId);
            return;
        }

        out.println("Enter the new loan date (yyyy-mm-dd):");
        Date loanDate = Date.valueOf(LocalDate.parse(readLine(in)));
        loan.setLoanDate(loanDate);

        out.println("Enter the new return date (yyyy-mm-dd):");
        Date returnDate = Date.valueOf(LocalDate.parse(readLine(in)));
        loan.setReturnDate(returnDate);

        loanRepository.save(loan);

        out.println("Updated loan successfully: " + loan);

        out.println();
        start();
    }

    private void showAllLoans() {
        Set<cat.uvic.teknos.library.models.Loan> loans = loanRepository.getAll();
        if (loans.isEmpty()) {
            out.println("No loans found.");
            return;
        }

        var asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("Book ID", "Customer ID", "Loan Date", "Return Date");
        asciiTable.addRule();

        for (cat.uvic.teknos.library.models.Loan loan : loans) {
            asciiTable.addRow(loan.getBook().getId(), loan.getCustomer().getId(), loan.getLoanDate(), loan.getReturnDate());
            asciiTable.addRule();
        }

        asciiTable.setTextAlignment(TextAlignment.CENTER);
        String render = asciiTable.render();
        out.println(render);

        out.println();
        start();
    }

    private void getLoanByBookId() {
        out.println("Enter the Book ID:");
        int bookId = Integer.parseInt(readLine(in));

        cat.uvic.teknos.library.models.Loan loan = loanRepository.get(bookId);
        if (loan != null) {
            out.println("Loan found: " + loan.getBook().getId() + " with Title: " + loan.getBook().getTitle());
            out.println("Customer: " + loan.getCustomer().getFirstName() + " " + loan.getCustomer().getLastName() + " with ID: " + loan.getCustomer().getId());
            out.println("Loan date: " + loan.getLoanDate());
            out.println("Return date: " + loan.getReturnDate());
        } else {
            out.println("Loan not found with Book ID: " + bookId);
        }

        out.println();
        start();
    }

    private void deleteLoanByBookId() {
        out.println("Enter the Book ID:");
        int bookId = Integer.parseInt(readLine(in));

        cat.uvic.teknos.library.models.Loan loan = loanRepository.get(bookId);
        if (loan != null) {
            try {
                loanRepository.delete(loan);
                out.println("Loan deleted successfully with Book ID: " + bookId);
            } catch (Exception e) {
                out.println("Failed to delete loan: " + e.getMessage());
                e.printStackTrace(out);
            }
        } else {
            out.println("Loan not found with Book ID: " + bookId);
        }

        out.println();
        start();
    }
}