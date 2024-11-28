package cat.uvic.teknos.library.domain.jdbc.models;

import cat.uvic.teknos.library.models.Book;


import java.util.Set;

public class Author implements cat.uvic.teknos.library.models.Author {
    private int id;
    private String firstName;
    private String lastName;
    private Set<Book> books;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Set<Book> getBooks() {
        return books;
    }

    @Override
    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}