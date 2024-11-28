package cat.uvic.teknos.library.domain.jpa.resources.models;

import cat.uvic.teknos.library.models.Author;
import cat.uvic.teknos.library.models.BookDetail;
import cat.uvic.teknos.library.models.Genre;
import cat.uvic.teknos.library.models.Loan;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "BOOK")
public class Book implements cat.uvic.teknos.library.models.Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOK_ID")
    private int id;

    @Column(name = "TITLE")
    private String title;

    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID")
    private Author author;

    @Column(name = "PUBLICATION_DATE")
    private Date publicationDate;

    @Column(name = "ISBN")
    private String ISBN;

    @Column(name = "COPIES")
    private int copies;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Genre> genres;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private BookDetail detail;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private Loan loan;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Author getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public Set<Genre> getGenres() {
        return genres;
    }

    @Override
    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public BookDetail getDetail() {
        return detail;
    }

    @Override
    public void setDetail(BookDetail detail) {
        this.detail = detail;
    }

    @Override
    public Loan getLoan() {
        return loan;
    }

    @Override
    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    @Override
    public Date getPublicationDate() {
        return publicationDate;
    }

    @Override
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Override
    public String getISBN() {
        return ISBN;
    }

    @Override
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    @Override
    public int getCopies() {
        return copies;
    }

    @Override
    public void setCopies(int copies) {
        this.copies = copies;
    }
}