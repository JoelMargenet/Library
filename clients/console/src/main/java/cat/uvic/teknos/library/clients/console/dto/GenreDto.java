package cat.uvic.teknos.library.clients.console.dto;

import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.Genre;

import java.util.HashSet;
import java.util.Set;

public class GenreDto implements Genre {
    private int id;
    private String name;
    private Set<Book> books = new HashSet<>();

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
    public Set<Book> getBooks() {
        return books;
    }

    @Override
    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
