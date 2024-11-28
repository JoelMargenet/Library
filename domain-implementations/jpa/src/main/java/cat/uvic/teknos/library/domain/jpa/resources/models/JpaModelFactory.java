package cat.uvic.teknos.library.domain.jpa.resources.models;

import cat.uvic.teknos.library.models.ModelFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.util.Properties;

public class JpaModelFactory implements ModelFactory {

    private final EntityManagerFactory entityManagerFactory;

    public JpaModelFactory() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("persistence.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        entityManagerFactory = Persistence.createEntityManagerFactory("PersistenceUnit", properties);
    }

    @Override
    public Author createAuthor() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new Author();
    }

    @Override
    public Book createBook() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new Book();
    }

    @Override
    public BookDetail createBookDetail() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new BookDetail();
    }

    @Override
    public Customer createCustomer() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new Customer();
    }

    @Override
    public Genre createGenre() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new Genre();
    }

    @Override
    public Loan createLoan() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new Loan();
    }
}