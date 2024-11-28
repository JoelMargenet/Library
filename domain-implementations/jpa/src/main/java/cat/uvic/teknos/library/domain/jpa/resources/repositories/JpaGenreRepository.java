package cat.uvic.teknos.library.domain.jpa.resources.repositories;

import cat.uvic.teknos.library.domain.jpa.resources.models.Genre;
import cat.uvic.teknos.library.repositories.GenreRepository;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaGenreRepository implements GenreRepository {

    private final EntityManager entityManager;

    public JpaGenreRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(cat.uvic.teknos.library.models.Genre model) {
        try {
            entityManager.getTransaction().begin();
            if (model.getId() <= 0) {
                entityManager.persist(model);
            } else if (!entityManager.contains(model)) {
                var genre = entityManager.find(Genre.class, model.getId());
                if (model.getName() == null || model.getName().isEmpty()) {
                    model.setName(genre.getName());
                }
                if (model.getBooks().isEmpty()) {
                    model.setBooks(genre.getBooks());
                }
                entityManager.merge(model);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.Genre model) {
        try {
            entityManager.getTransaction().begin();
            cat.uvic.teknos.library.models.Genre genre = entityManager.find(Genre.class, model.getId());
            entityManager.remove(genre);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("ERROR");
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Genre get(Integer id) {
        return entityManager.find(Genre.class, id);
    }

    @Override
    public Set<cat.uvic.teknos.library.models.Genre> getAll() {
        List<cat.uvic.teknos.library.models.Genre> genreList = entityManager.createQuery("SELECT g FROM Genre g", cat.uvic.teknos.library.models.Genre.class).getResultList();
        return new HashSet<>(genreList);
    }
}