package cat.uvic.teknos.library.domain.jpa.resources.models;

import cat.uvic.teknos.library.models.Book;
import jakarta.persistence.*;

@Entity
@Table(name = "BOOK_DETAIL")
public class BookDetail implements cat.uvic.teknos.library.models.BookDetail {

    @Id
    @Column(name = "BOOK_ID")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Book book;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "REVIEWS", columnDefinition = "TEXT")
    private String reviews;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Book getBook() {
        return book;
    }

    @Override
    public void setBook(Book book) {
        this.book = book;
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
}