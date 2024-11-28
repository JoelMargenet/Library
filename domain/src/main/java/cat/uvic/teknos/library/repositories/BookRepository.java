package cat.uvic.teknos.library.repositories;

import cat.uvic.teknos.library.models.Book;

import java.util.Set;

public interface BookRepository extends Repository<Integer, Book> {
    @Override
    void save(Book model);

    @Override
    void delete(Book model);

    @Override
    Book get(Integer id);

    @Override
    Set<Book> getAll();
}