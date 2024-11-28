package cat.uvic.teknos.library.domain.jpa.resources.repositories;

import cat.uvic.teknos.library.domain.jpa.resources.models.Author;
import cat.uvic.teknos.library.repositories.AuthorRepository;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaAuthorRepository implements AuthorRepository {

    private final EntityManager entityManager;

    public JpaAuthorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(cat.uvic.teknos.library.models.Author model) {
        try {
            entityManager.getTransaction().begin();
            if (model.getId() <= 0) {
                entityManager.persist(model);
            } else if (!entityManager.contains(model)) {
                var author = entityManager.find(Author.class, model.getId());
                if (model.getFirstName() == null || model.getFirstName().isEmpty()) {
                    model.setFirstName(author.getFirstName());
                }
                if (model.getLastName() == null || model.getLastName().isEmpty()) {
                    model.setLastName(author.getLastName());
                }
                if (model.getBooks().isEmpty()) {
                    model.setBooks(author.getBooks());
                }
                entityManager.merge(model);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.Author model) {
        try {
            entityManager.getTransaction().begin();
            cat.uvic.teknos.library.models.Author author = entityManager.find(Author.class, model.getId());
            entityManager.remove(author);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("ERROR");
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Author get(Integer id) {
        return entityManager.find(Author.class, id);
    }

    @Override
    public Set<cat.uvic.teknos.library.models.Author> getAll() {
        List<cat.uvic.teknos.library.models.Author> authorList = entityManager.createQuery("SELECT a FROM Author a", cat.uvic.teknos.library.models.Author.class).getResultList();
        return new HashSet<>(authorList);
    }
}