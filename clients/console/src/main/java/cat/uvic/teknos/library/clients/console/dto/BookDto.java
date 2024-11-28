package cat.uvic.teknos.library.clients.console.dto;

import cat.uvic.teknos.library.models.*;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class BookDto implements Book {
    private int id;
    private String title;
    private Date publicationDate;
    private String isbn;
    private int copies;
    private Author author;
    private Set<Genre> genres = new HashSet<>();
    private BookDetail detail;
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
    public Date getPublicationDate() {
        return publicationDate;
    }

    @Override
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Override
    public String getISBN() {
        return isbn;
    }

    @Override
    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public int getCopies() {
        return copies;
    }

    @Override
    public void setCopies(int copies) {
        this.copies = copies;
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
}
