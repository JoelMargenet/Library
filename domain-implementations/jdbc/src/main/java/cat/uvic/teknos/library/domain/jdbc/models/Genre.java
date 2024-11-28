package cat.uvic.teknos.library.domain.jdbc.models;


import cat.uvic.teknos.library.models.Book;

import java.util.Set;

public class Genre implements cat.uvic.teknos.library.models.Genre {
    private int id;
    private String name;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<Book> getBooks() { return books; }

    @Override
    public void setBooks(Set<Book> books) { this.books = books; }


}