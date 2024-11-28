package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.exceptions.RepositoryException;

import cat.uvic.teknos.library.repositories.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcRepositoryFactory implements RepositoryFactory {
    private Connection connection;

    public JdbcRepositoryFactory() {
        try {
            var properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/datasource.properties"));

            connection = DriverManager.getConnection(String.format("%s:%s://%s/%s",
                    properties.getProperty("protocol"),
                    properties.getProperty("subprotocol"),
                    properties.getProperty("url"),
                    properties.getProperty("database")), properties.getProperty("user"), properties.getProperty("password"));
        } catch (SQLException e) {
            throw new RepositoryException(e);
        } catch (IOException e) {
            throw new RepositoryException();
        }
    }

    @Override
    public BookRepository getBookRepository() {
        return new JdbcBookRepository(connection);
    }

    @Override
    public AuthorRepository getAuthorRepository() {
        return new JdbcAuthorRepository(connection);
    }

    @Override
    public BookDetailRepository getBookDetailRepository() {
        return new JdbcBookDetailRepository(connection);
    }

    @Override
    public CustomerRepository getCustomerRepository() {
        return new JdbcCustomerRepository(connection);
    }

    @Override
    public GenreRepository getGenreRepository() {
        return new JdbcGenreRepository(connection);
    }

    @Override
    public LoanRepository getLoanRepository() {
        return new JdbcLoanRepository(connection);
    }
}
