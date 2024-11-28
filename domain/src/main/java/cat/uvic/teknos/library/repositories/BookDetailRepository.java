package cat.uvic.teknos.library.repositories;

import cat.uvic.teknos.library.models.BookDetail;

import java.util.Set;

public interface BookDetailRepository extends Repository<Integer, BookDetail> {
    @Override
    void save(BookDetail model);

    @Override
    void delete(BookDetail model);


    @Override
    BookDetail get(Integer id);

    @Override
    Set<BookDetail> getAll();
}