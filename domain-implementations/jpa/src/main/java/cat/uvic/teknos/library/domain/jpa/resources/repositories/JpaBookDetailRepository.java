package cat.uvic.teknos.library.domain.jpa.resources.repositories;

import cat.uvic.teknos.library.domain.jpa.resources.models.BookDetail;
import cat.uvic.teknos.library.repositories.BookDetailRepository;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaBookDetailRepository implements BookDetailRepository {

    private final EntityManager entityManager;

    public JpaBookDetailRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(cat.uvic.teknos.library.models.BookDetail model) {
        try {
            entityManager.getTransaction().begin();
            if (model.getId() <= 0) {
                entityManager.persist(model);
            } else if (!entityManager.contains(model)) {
                var bookDetail = entityManager.find(BookDetail.class, model.getId());
                if (model.getDescription() == null || model.getDescription().isEmpty()) {
                    model.setDescription(bookDetail.getDescription());
                }
                if (model.getReviews() == null || model.getReviews().isEmpty()) {
                    model.setReviews(bookDetail.getReviews());
                }
                if (model.getBook() == null) {
                    model.setBook(bookDetail.getBook());
                }
                entityManager.merge(model);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.BookDetail model) {
        try {
            entityManager.getTransaction().begin();
            cat.uvic.teknos.library.models.BookDetail bookDetail = entityManager.find(BookDetail.class, model.getId());
            entityManager.remove(bookDetail);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("ERROR");
        }
    }

    @Override
    public cat.uvic.teknos.library.models.BookDetail get(Integer id) {
        return entityManager.find(BookDetail.class, id);
    }

    @Override
    public Set<cat.uvic.teknos.library.models.BookDetail> getAll() {
        List<cat.uvic.teknos.library.models.BookDetail> bookDetailList = entityManager.createQuery("SELECT bd FROM BookDetail bd", cat.uvic.teknos.library.models.BookDetail.class).getResultList();
        return new HashSet<>(bookDetailList);
    }
}