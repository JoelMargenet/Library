package cat.uvic.teknos.library.clients.console.dto;

import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.BookDetail;

public class BookDetailDto implements BookDetail {
    private int id;
    private String description;
    private String reviews;
    private Book book;

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
        this.reviews = reviews;
    }

    @Override
    public Book getBook() {
        return book;
    }

    @Override
    public void setBook(Book book) {
        this.book = book;
    }
}
