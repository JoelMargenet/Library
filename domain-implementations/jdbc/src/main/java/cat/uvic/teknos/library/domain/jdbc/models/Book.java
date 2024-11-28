package cat.uvic.teknos.library.domain.jdbc.models;

import cat.uvic.teknos.library.models.Author;
import cat.uvic.teknos.library.models.BookDetail;
import cat.uvic.teknos.library.models.Genre;
import cat.uvic.teknos.library.models.Loan;

import java.sql.Date;
import java.util.Set;

public class Book implements cat.uvic.teknos.library.models.Book {
    private int id;
    private String title;
    private Date publicationDate;
    private String isbn;
    private int copies;
    private Author author;
    private Set<Genre> genres;
    private Loan loan;
    private BookDetail bookDetail;

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
    public BookDetail getDetail() {
        return bookDetail;
    }

    @Override
    public void setDetail(BookDetail bookDetail) {
        this.bookDetail = bookDetail;
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
    public Set<Genre> getGenres() {
        return genres;
    }

    @Override
    public void setGenres(Set<Genre> genre) {
        this.genres = genre;
    }

}