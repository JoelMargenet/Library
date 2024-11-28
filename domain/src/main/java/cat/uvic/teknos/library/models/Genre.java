package cat.uvic.teknos.library.models;

import java.util.Set;

public interface Genre {
    int getId();
    void setId(int id);

    String getName();
    void setName(String name);

    Set<Book> getBooks();
    void setBooks(Set<Book> books);
}
