package cat.uvic.teknos.library.domain.jpa.resources.repositories;


import cat.uvic.teknos.library.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaRepositoryFactory implements RepositoryFactory {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public JpaRepositoryFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("libraryPU");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public AuthorRepository getAuthorRepository() {
        return new JpaAuthorRepository(entityManager);
    }

    @Override
    public BookRepository getBookRepository() {
        return new JpaBookRepository(entityManager);
    }

    @Override
    public BookDetailRepository getBookDetailRepository() {
        return new JpaBookDetailRepository(entityManager);
    }

    @Override
    public CustomerRepository getCustomerRepository() {
        return new JpaCustomerRepository(entityManager);
    }

    @Override
    public GenreRepository getGenreRepository() {
        return new JpaGenreRepository(entityManager);
    }

    @Override
    public LoanRepository getLoanRepository() {
        return new JpaLoanRepository(entityManager);
    }

    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }
}