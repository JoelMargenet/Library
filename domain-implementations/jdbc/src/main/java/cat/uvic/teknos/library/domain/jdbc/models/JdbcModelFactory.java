package cat.uvic.teknos.library.domain.jdbc.models;

import cat.uvic.teknos.library.models.Author;
import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.BookDetail;
import cat.uvic.teknos.library.models.Customer;
import cat.uvic.teknos.library.models.Genre;
import cat.uvic.teknos.library.models.Loan;
import cat.uvic.teknos.library.models.ModelFactory;

public class JdbcModelFactory implements ModelFactory {

    @Override
    public Author createAuthor() {
        return new cat.uvic.teknos.library.domain.jdbc.models.Author();
    }

    @Override
    public Book createBook() {
        return new cat.uvic.teknos.library.domain.jdbc.models.Book();
    }

    @Override
    public BookDetail createBookDetail() {
        return new cat.uvic.teknos.library.domain.jdbc.models.BookDetail();
    }

    @Override
    public Customer createCustomer() {
        return new cat.uvic.teknos.library.domain.jdbc.models.Customer();
    }

    @Override
    public Genre createGenre() {
        return new cat.uvic.teknos.library.domain.jdbc.models.Genre();
    }

    @Override
    public Loan createLoan() {
        return new cat.uvic.teknos.library.domain.jdbc.models.Loan();
    }
}
