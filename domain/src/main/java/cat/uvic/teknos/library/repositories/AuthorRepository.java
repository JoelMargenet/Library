package cat.uvic.teknos.library.repositories;

import cat.uvic.teknos.library.models.Author;

import java.util.Set;

public interface AuthorRepository  extends Repository<Integer, Author> {
    @Override
    void save(Author model);

    @Override
    void delete(Author model);

    @Override
    Author get(Integer id);

    @Override
    Set<Author> getAll();
}
