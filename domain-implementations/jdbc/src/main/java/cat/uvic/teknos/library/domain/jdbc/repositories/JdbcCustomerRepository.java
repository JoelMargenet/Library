package cat.uvic.teknos.library.domain.jdbc.repositories;


import cat.uvic.teknos.library.domain.jdbc.models.Customer;
import cat.uvic.teknos.library.domain.jdbc.models.Loan;
import cat.uvic.teknos.library.repositories.CustomerRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


//will try to do this one with a (ON DUPLICATED KEY) when inserting, that way there will be no need to make an update method
public class JdbcCustomerRepository implements CustomerRepository {

    private static final String INSERT_CUSTOMER = "INSERT INTO CUSTOMER (FIRST_NAME, LAST_NAME, EMAIL, ADDRESS, PHONE_NUMBER) VALUES (?,?,?,?,?)";
    private final Connection connection;

    public JdbcCustomerRepository(Connection connection){
        this.connection = connection;
    }



    @Override
    public void save(cat.uvic.teknos.library.models.Customer model) {
        if(model.getId()<=0){
            insert(model);
        }else{
            update(model);
        }
    }


    private void insert(cat.uvic.teknos.library.models.Customer model) {
        try (var statement = connection.prepareStatement(INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS)) {

            connection.setAutoCommit(false);

            statement.setString(1, model.getFirstName());
            statement.setString(2, model.getLastName());
            statement.setString(3, model.getEmail());
            statement.setString(4, model.getAddress());
            statement.setString(5, model.getPhoneNumber());

            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                model.setId(keys.getInt(1));
            }

            connection.commit();

        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    private void update(cat.uvic.teknos.library.models.Customer model) {
        try (var preparedStatement = connection.prepareStatement(
                "UPDATE CUSTOMER SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, ADDRESS = ?, PHONE_NUMBER = ? WHERE CUSTOMER_ID = ?")) {
            connection.setAutoCommit(false);

            preparedStatement.setString(1, model.getFirstName());
            preparedStatement.setString(2, model.getLastName());
            preparedStatement.setString(3, model.getEmail());
            preparedStatement.setString(4, model.getAddress());
            preparedStatement.setString(5, model.getPhoneNumber());
            preparedStatement.setInt(6, model.getId());

            preparedStatement.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.Customer customer) {
        try {
            connection.setAutoCommit(false);

            // Delete associated loans first
            deleteLoansByCustomerId(customer.getId());

            // Now delete the customer
            String query = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, customer.getId());
                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    public void deleteLoansByCustomerId(int customerId) {
        String query = "DELETE FROM LOAN WHERE CUSTOMER_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, customerId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Customer get(Integer id) {
        String query = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setInt(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cat.uvic.teknos.library.models.Customer result = new Customer();
                    result.setId(resultSet.getInt("CUSTOMER_ID"));
                    result.setFirstName(resultSet.getString("FIRST_NAME"));
                    result.setLastName(resultSet.getString("LAST_NAME"));
                    result.setEmail(resultSet.getString("EMAIL"));
                    result.setAddress(resultSet.getString("ADDRESS"));
                    result.setPhoneNumber(resultSet.getString("PHONE_NUMBER"));

                    return result;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    //check getAll
    @Override
    public Set<cat.uvic.teknos.library.models.Customer> getAll() {
        String query = "SELECT * FROM CUSTOMER";


        try (PreparedStatement statement = connection.prepareStatement(query);
        ) {

            int id = 1;
            try (var resultSet = statement.executeQuery()) {

                var customers = new HashSet<cat.uvic.teknos.library.models.Customer>();

                while (resultSet.next()) {

                    id++;
                    cat.uvic.teknos.library.models.Customer result = new Customer();
                    result.setId(resultSet.getInt("CUSTOMER_ID"));
                    result.setFirstName(resultSet.getString("FIRST_NAME"));
                    result.setLastName(resultSet.getString("LAST_NAME"));
                    result.setEmail(resultSet.getString("EMAIL"));
                    result.setAddress(resultSet.getString("ADDRESS"));
                    result.setPhoneNumber(resultSet.getString("PHONE_NUMBER"));


                    customers.add(result);

                }
                return customers;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Set<Loan> getAllLoansByCustomerId(int customerId) {
        Set<Loan> loans = new HashSet<>();
        String query = "SELECT * FROM LOAN WHERE CUSTOMER_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, customerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Loan loan = new Loan();
                    loan.setId(resultSet.getInt("BOOK_ID"));
                    loan.getCustomer().setId(resultSet.getInt("CUSTOMER_ID"));
                    loan.setLoanDate(resultSet.getDate("LOAN_DATE"));
                    loan.setReturnDate(resultSet.getDate("RETURN_DATE"));
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }


    private void setAutoCommitTrue() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void rollback() {
        try{
            connection.rollback();
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}