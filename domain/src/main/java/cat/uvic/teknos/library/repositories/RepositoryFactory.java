package cat.uvic.teknos.library.repositories;

public interface RepositoryFactory {
    AuthorRepository getAuthorRepository();
    BookRepository  getBookRepository();
    BookDetailRepository getBookDetailRepository();
    CustomerRepository  getCustomerRepository();
    GenreRepository getGenreRepository();
    LoanRepository getLoanRepository();
}
