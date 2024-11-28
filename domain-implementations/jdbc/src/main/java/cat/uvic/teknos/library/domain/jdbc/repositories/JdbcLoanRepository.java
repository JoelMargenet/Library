package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.domain.jdbc.models.Book;
import cat.uvic.teknos.library.domain.jdbc.models.Customer;
import cat.uvic.teknos.library.domain.jdbc.models.Loan;
import cat.uvic.teknos.library.repositories.LoanRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class JdbcLoanRepository implements LoanRepository {

    private static final String INSERT_LOAN = "INSERT INTO LOAN (BOOK_ID, CUSTOMER_ID, LOAN_DATE, RETURN_DATE) VALUES (?,?,?,?)";
    private static final String UPDATE_LOAN = "UPDATE LOAN SET CUSTOMER_ID = ?, LOAN_DATE = ?, RETURN_DATE = ? WHERE BOOK_ID = ?";
    private static final String DELETE_LOAN = "DELETE FROM LOAN WHERE BOOK_ID = ?";
    private static final String SELECT_LOAN = "SELECT * FROM LOAN WHERE BOOK_ID = ?";
    private static final String SELECT_ALL_LOANS = "SELECT * FROM LOAN";

    private final Connection connection;

    public JdbcLoanRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(cat.uvic.teknos.library.models.Loan loan) {
        if (get(loan.getBook().getId()) == null) {
            insert(loan);
        } else {
            update(loan);
        }
    }

    private void insert(cat.uvic.teknos.library.models.Loan loan) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_LOAN)) {
            statement.setInt(1, loan.getBook().getId());
            statement.setInt(2, loan.getCustomer().getId());
            statement.setDate(3, loan.getLoanDate());
            statement.setDate(4, loan.getReturnDate());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(cat.uvic.teknos.library.models.Loan loan) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_LOAN)) {
            statement.setInt(1, loan.getCustomer().getId());
            statement.setDate(2, loan.getLoanDate());
            statement.setDate(3, loan.getReturnDate());
            statement.setInt(4, loan.getBook().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.Loan loan) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_LOAN)) {
            statement.setInt(1, loan.getBook().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }@Override
    public cat.uvic.teknos.library.models.Loan get(Integer bookId) {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_LOAN)) {
            statement.setInt(1, bookId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cat.uvic.teknos.library.models.Loan loan = new Loan();
                    cat.uvic.teknos.library.models.Book book = new Book();
                    book.setId(bookId);
                    loan.setBook(book);
                    loan.setLoanDate(resultSet.getDate("LOAN_DATE"));
                    loan.setReturnDate(resultSet.getDate("RETURN_DATE"));

                    int customerId = resultSet.getInt("CUSTOMER_ID");
                    cat.uvic.teknos.library.models.Customer customer = getCustomerById(customerId);
                    loan.setCustomer(customer);

                    return loan;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private cat.uvic.teknos.library.models.Customer getCustomerById(int customerId) {
        String customerQuery = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(customerQuery)) {
            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cat.uvic.teknos.library.models.Customer customer = new Customer();
                    customer.setId(customerId);
                    customer.setFirstName(resultSet.getString("FIRST_NAME"));
                    customer.setLastName(resultSet.getString("LAST_NAME"));
                    customer.setEmail(resultSet.getString("EMAIL"));
                    customer.setAddress(resultSet.getString("ADDRESS"));
                    customer.setPhoneNumber(resultSet.getString("PHONE_NUMBER"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    @Override
    public Set<cat.uvic.teknos.library.models.Loan> getAll() {
        Set<cat.uvic.teknos.library.models.Loan> loans = new HashSet<>();
        String loanQuery = "SELECT * FROM LOAN";
        String bookQuery = "SELECT * FROM BOOK WHERE BOOK_ID = ?";
        String customerQuery = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(loanQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                cat.uvic.teknos.library.models.Loan loan = new Loan();
                int bookId = resultSet.getInt("BOOK_ID");
                int customerId = resultSet.getInt("CUSTOMER_ID");
                loan.setLoanDate(resultSet.getDate("LOAN_DATE"));
                loan.setReturnDate(resultSet.getDate("RETURN_DATE"));

                // Fetch the book
                try (PreparedStatement bookStatement = connection.prepareStatement(bookQuery)) {
                    bookStatement.setInt(1, bookId);
                    try (ResultSet bookResultSet = bookStatement.executeQuery()) {
                        if (bookResultSet.next()) {
                            cat.uvic.teknos.library.models.Book book = new Book();
                            book.setId(bookId);
                            loan.setBook(book);
                        }
                    }
                }

                // Fetch the customer
                try (PreparedStatement customerStatement = connection.prepareStatement(customerQuery)) {
                    customerStatement.setInt(1, customerId);
                    try (ResultSet customerResultSet = customerStatement.executeQuery()) {
                        if (customerResultSet.next()) {
                            cat.uvic.teknos.library.models.Customer customer = new Customer();
                            customer.setId(customerId);
                            loan.setCustomer(customer);
                        }
                    }
                }

                loans.add(loan);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }
}