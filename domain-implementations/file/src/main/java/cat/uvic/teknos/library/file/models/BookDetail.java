package cat.uvic.teknos.library.file.models;

import cat.uvic.teknos.library.models.Book;

import java.io.Serializable;

public class BookDetail implements cat.uvic.teknos.library.models.BookDetail, Serializable {
    private int id;
    private String description;
    private String reviews;
    private Book book;
    private String isbn;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getReviews() {
        return reviews;
    }

    @Override
    public void setReviews(String reviews) {
        this.reviews += reviews;
    }

    @Override
    public Book getBook() { return book; }

    @Override
    public void setBook(Book book) {
        this.book = book;
    }

}