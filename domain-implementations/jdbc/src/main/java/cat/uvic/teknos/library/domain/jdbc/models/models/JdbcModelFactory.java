package cat.uvic.teknos.library.domain.jdbc.models.models;


import cat.uvic.teknos.library.models.ModelFactory;
import cat.uvic.teknos.library.domain.jdbc.models.*;

public class JdbcModelFactory implements ModelFactory {

    @Override
    public cat.uvic.teknos.library.models.Author createAuthor() {
        return new Author();
    }

    @Override
    public cat.uvic.teknos.library.models.Book createBook() {
        return new Book();
    }

    @Override
    public cat.uvic.teknos.library.models.BookDetail createBookDetail() {
        return new BookDetail();
    }

    @Override
    public cat.uvic.teknos.library.models.Customer createCustomer() {
        return new Customer();
    }

    @Override
    public cat.uvic.teknos.library.models.Genre createGenre() {
        return new Genre();
    }

    @Override
    public cat.uvic.teknos.library.models.Loan createLoan() {
        return new Loan();
    }
}
