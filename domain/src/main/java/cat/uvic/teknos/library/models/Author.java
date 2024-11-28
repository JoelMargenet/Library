package cat.uvic.teknos.library.models;

import java.util.Set;

public interface Author {
    int getId();
    void setId(int id);

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    Set<Book> getBooks();
    void setBooks(Set<Book> books);
}