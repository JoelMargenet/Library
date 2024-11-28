package cat.library.services;

import cat.library.services.controllers.Controller;
import cat.uvic.teknos.library.domain.jdbc.repositories.JdbcRepositoryFactory;
import cat.library.services.controllers.AuthorController;
import cat.library.services.controllers.BookController;
import cat.library.services.controllers.BookDetailController;
import cat.library.services.controllers.CustomerController;
import cat.library.services.controllers.GenreController;
import cat.library.services.controllers.LoanController;
import cat.uvic.teknos.library.repositories.AuthorRepository;
import cat.uvic.teknos.library.repositories.BookDetailRepository;
import cat.uvic.teknos.library.repositories.BookRepository;
import cat.uvic.teknos.library.repositories.CustomerRepository;
import cat.uvic.teknos.library.repositories.GenreRepository;
import cat.uvic.teknos.library.repositories.LoanRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws IOException {
        // Create the repository factory
        var repositoryFactory = new JdbcRepositoryFactory();

        // Get specific repositories
        AuthorRepository authorRepository = repositoryFactory.getAuthorRepository();
        BookRepository bookRepository = repositoryFactory.getBookRepository();
        BookDetailRepository bookDetailRepository = repositoryFactory.getBookDetailRepository();
        CustomerRepository customerRepository = repositoryFactory.getCustomerRepository();
        GenreRepository genreRepository = repositoryFactory.getGenreRepository();
        LoanRepository loanRepository = repositoryFactory.getLoanRepository();

        // Create controllers using the appropriate repositories
        var authorController = new AuthorController(authorRepository);
        var bookController = new BookController(bookRepository);
        var bookDetailController = new BookDetailController(bookDetailRepository);
        var customerController = new CustomerController(customerRepository);
        var genreController = new GenreController(genreRepository);
        var loanController = new LoanController(loanRepository);

        // Map the controllers to their corresponding route identifiers
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("author", authorController);
        controllers.put("book", bookController);
        controllers.put("bookDetail", bookDetailController);
        controllers.put("customer", customerController);
        controllers.put("genre", genreController);
        controllers.put("loan", loanController);

        // Initialize the request router and server
        var requestRouter = new RequestRouterImplementation(controllers);
        var server = new Server(requestRouter);
        server.start();
    }
}
