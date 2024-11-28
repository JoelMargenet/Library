package cat.uvic.teknos.library.clients.console.dto;

import cat.uvic.teknos.library.models.*;

public class DtoModelFactory implements ModelFactory {
    @Override
    public Author createAuthor() {
        return new AuthorDto();
    }

    @Override
    public Book createBook() {
        return new BookDto();
    }

    @Override
    public BookDetail createBookDetail() {
        return new BookDetailDto();
    }

    @Override
    public Customer createCustomer() {
        return new CustomerDto();
    }

    @Override
    public Genre createGenre() {
        return new GenreDto();
    }

    @Override
    public Loan createLoan() {
        return new LoanDto();
    }
}
