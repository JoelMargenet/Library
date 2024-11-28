package cat.uvic.teknos.library.domain.jpa.resources.repositories;

import cat.uvic.teknos.library.domain.jpa.resources.models.Book;
import cat.uvic.teknos.library.repositories.BookRepository;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaBookRepository implements BookRepository {

    private final EntityManager entityManager;

    public JpaBookRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(cat.uvic.teknos.library.models.Book model) {
        try {
            entityManager.getTransaction().begin();
            if (model.getId() <= 0) {
                entityManager.persist(model);
            } else if (!entityManager.contains(model)) {
                var book = entityManager.find(Book.class, model.getId());
                if (model.getTitle() == null || model.getTitle().isEmpty()) {
                    model.setTitle(book.getTitle());
                }
                if (model.getAuthor() == null) {
                    model.setAuthor(book.getAuthor());
                }
                if (model.getPublicationDate() == null) {
                    model.setPublicationDate(book.getPublicationDate());
                }
                if (model.getISBN() == null || model.getISBN().isEmpty()) {
                    model.setISBN(book.getISBN());
                }
                if (model.getCopies() <= 0) {
                    model.setCopies(book.getCopies());
                }
                if (model.getGenres().isEmpty()) {
                    model.setGenres(book.getGenres());
                }
                if (model.getDetail() == null) {
                    model.setDetail(book.getDetail());
                }
                if (model.getLoan() == null) {
                    model.setLoan(book.getLoan());
                }
                entityManager.merge(model);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.Book model) {
        try {
            entityManager.getTransaction().begin();
            cat.uvic.teknos.library.models.Book book = entityManager.find(Book.class, model.getId());
            entityManager.remove(book);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("ERROR");
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Book get(Integer id) {
        return entityManager.find(Book.class, id);
    }

    @Override
    public Set<cat.uvic.teknos.library.models.Book> getAll() {
        List<cat.uvic.teknos.library.models.Book> bookList = entityManager.createQuery("SELECT b FROM Book b", cat.uvic.teknos.library.models.Book.class).getResultList();
        return new HashSet<>(bookList);
    }
}