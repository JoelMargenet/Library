package cat.uvic.teknos.library.clients.console;

import cat.uvic.teknos.library.clients.console.dto.*;
import cat.uvic.teknos.library.clients.console.exceptions.RequestException;
import cat.uvic.teknos.library.clients.console.utils.Mappers;
import cat.uvic.teknos.library.clients.console.utils.RestClient;
import cat.uvic.teknos.library.clients.console.utils.RestClientImpl;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Date;
import java.util.stream.Collectors;

public class App {
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final PrintStream out = new PrintStream(System.out);
    private static RestClient restClient = new RestClientImpl("localhost", 8080);

    public static void main(String[] args) {
        showBanner();

        var command = "";
        do {
            showMainMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> manageAuthors();
                case "2" -> manageBooks();
                case "3" -> manageBookDetails();
                case "4" -> manageCustomers();
                case "5" -> manageGenres();
                case "6" -> manageLoans();
            }

        } while (!command.equals("exit"));

        out.println("Bye!");
    }

    // Method to read input
    private static String readLine(BufferedReader in) {
        String command;
        try {
            command = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error while reading the menu option", e);
        }
        return command;
    }

    // Show Banner
    private static void showBanner() {
        var bannerStream = App.class.getResourceAsStream("/banner.txt");

        var banner = new BufferedReader(new InputStreamReader(bannerStream))
                .lines().collect(Collectors.joining("\n"));

        System.out.println(banner);
    }

    // Show main menu
    private static void showMainMenu() {
        out.println("\nMain Menu");
        out.println("1. Author");
        out.println("2. Book");
        out.println("3. Book Detail");
        out.println("4. Customer");
        out.println("5. Genre");
        out.println("6. Loan");
        out.println("Type 'exit' to quit.");
        out.print(">> ");
    }

    // Managing Authors
    private static void manageAuthors() {
        var command = "";
        do {
            showAuthorSubMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> listAuthors();
                case "2" -> viewAuthorById();
                case "3" -> createAuthor();
                case "4" -> updateAuthor();
                case "5" -> deleteAuthor();
            }

        } while (!command.equals("exit"));
    }

    private static void showAuthorSubMenu() {
        out.println("\nAuthor Menu");
        out.println("1. List all Authors");
        out.println("2. View Author by ID");
        out.println("3. Create a new Author");
        out.println("4. Update an Author");
        out.println("5. Delete an Author");
        out.println("Type 'exit' to go back.");
        out.print(">> ");
    }

    private static void listAuthors() {
        try {
            var authors = restClient.getAll("/author/", AuthorDto[].class);
            for (AuthorDto author : authors) {
                out.print("-----------------------");
                out.println("\nAuthor ID: " + author.getId());
                out.println("Name: " + author.getFirstName());
                out.println("Last Name: " + author.getLastName());
            }
            out.print("-----------------------\n");
        } catch (RequestException e) {
            out.println("Error fetching authors: " + e.getMessage());
        }
    }

    private static void viewAuthorById() {
        out.println("\nAuthor Id to search");
        out.print(">> ");
        var authorId = readLine(in);
        try {
            var author = restClient.get("/author/" + authorId, AuthorDto.class);
            out.print("-----------------------");
            out.println("\nAuthor ID: " + author.getId());
            out.println("Name: " + author.getFirstName());
            out.println("Last Name: " + author.getLastName());
        } catch (RequestException e) {
            out.println("Error fetching author: " + e.getMessage());
        }
        out.print("-----------------------\n");
    }

    private static void createAuthor() {
        var author = new AuthorDto();
        out.println("\nAuthor First Name");
        out.print(">> ");
        author.setFirstName(readLine(in));
        out.println("Author Last Name");
        out.print(">> ");
        author.setLastName(readLine(in));

        try {
            restClient.post("/author", Mappers.get().writeValueAsString(author));
            out.print("Author created successfully.\n");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error creating author: " + e.getMessage());
        }
    }

    private static void updateAuthor() {
        out.println("Author Id to update");
        out.print(">> ");
        var authorId = readLine(in);
        try {
            var author = restClient.get("/author/" + authorId, AuthorDto.class);
            out.println("Updating Author: " + author.getFirstName() + " " + author.getLastName());
            out.println("New Author First Name:");
            out.print(">>");
            author.setFirstName(readLine(in));  // Modify first name
            out.println("New Author Last Name:");
            out.print(">> ");
            author.setLastName(readLine(in));   // Modify last name

            restClient.put("/author/" + authorId, Mappers.get().writeValueAsString(author));
            out.print("Author updated successfully.\n");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error updating author: " + e.getMessage());
        }
    }

    private static void deleteAuthor() {
        out.println("Enter the Id to delete:");
        out.print(">> ");
        var authorId = readLine(in);
        try {
            restClient.delete("/author/" + authorId);
            out.print("Author deleted successfully.\n");
        } catch (RequestException e) {
            out.println("Error deleting author: " + e.getMessage());
        }
    }
    // Managing Books
    private static void manageBooks() {
        var command = "";
        do {
            showBookSubMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> listBooks();
                case "2" -> viewBookById();
                case "3" -> createBook();
                case "4" -> updateBook();
                case "5" -> deleteBook();
            }

        } while (!command.equals("exit"));
    }

    private static void showBookSubMenu() {
        out.println("\nBook Menu");
        out.println("1. List all Books");
        out.println("2. View Book by ID");
        out.println("3. Create a new Book");
        out.println("4. Update a Book");
        out.println("5. Delete a Book");
        out.println("Type 'exit' to go back.");
        out.print(">> ");
    }

    private static void listBooks() {
        try {
            var books = restClient.getAll("/book", BookDto[].class);
            for (BookDto book : books) {
                out.print("-----------------------");
                out.println("\nBook ID: " + book.getId());
                out.println("Title: " + book.getTitle());
                out.println("Author: " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
                out.println("Publication Date: " + book.getPublicationDate());
                out.println("ISBN: " + book.getISBN());
                out.println("Copies: " + book.getCopies());
            }
            out.print("-----------------------\n");
        } catch (RequestException e) {
            out.println("Error fetching books: " + e.getMessage());
        }
    }

    private static void viewBookById() {
        out.println("\nBook ID to view");
        out.print(">> ");
        var bookId = readLine(in);
        try {
            var book = restClient.get("/book/" + bookId, BookDto.class);
            out.print("-----------------------");
            out.println("\nBook ID: " + book.getId());
            out.println("Title: " + book.getTitle());
            out.println("Author: " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
            out.println("Publication Date: " + book.getPublicationDate());
            out.println("ISBN: " + book.getISBN());
            out.println("Copies: " + book.getCopies());
            out.print("-----------------------\n");
        } catch (RequestException e) {
            out.println("Error fetching book: " + e.getMessage());
        }
    }

    private static void createBook() {
        var book = new BookDto();
        out.println("\nBook Title:");
        out.print(">> ");
        book.setTitle(readLine(in));
        book.setAuthor(getAuthorFromInput());
        out.println("Publication Date: ");
        out.print(">> ");
        book.setPublicationDate(Date.valueOf(readLine(in)));
        out.println("Book ISBN:");
        out.print(">> ");
        book.setISBN(readLine(in));
        out.println("Book number of Copies:");
        out.print(">> ");
        book.setCopies(Integer.parseInt(readLine(in)));  // Convert copies to int

        try {
            restClient.post("/book", Mappers.get().writeValueAsString(book));
            out.println("Book created successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error creating book: " + e.getMessage());
        }
    }

    private static AuthorDto getAuthorFromInput() {
        out.println("Enter Author ID: ");
        out.print(">> ");
        var authorId = readLine(in);
        try {
            return restClient.get("/author/" + authorId, AuthorDto.class);
        } catch (RequestException e) {
            out.println("Error fetching author: " + e.getMessage());
            return null;
        }
    }

    private static void updateBook() {
        out.println("\nBook ID to update");
        out.print(">> ");
        var bookId = readLine(in);
        try {
            var book = restClient.get("/book/" + bookId, BookDto.class);
            out.println("Updating Book: " + book.getTitle());
            out.println("New Book Title:");
            out.print(">> ");
            book.setTitle(readLine(in));
            out.println("New Book Author:");
            book.setAuthor(getAuthorFromInput());
            out.println("New Book Publication Date: ");
            out.print(">> ");
            book.setPublicationDate(Date.valueOf(readLine(in)));
            out.println("New Book ISBN:");
            out.print(">> ");
            book.setISBN(readLine(in));
            out.println("New Book number of Copies:");
            out.print(">> ");
            book.setCopies(Integer.parseInt(readLine(in)));  // Modify copies

            restClient.put("/book/" + bookId, Mappers.get().writeValueAsString(book));
            out.println("Book updated successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error updating book: " + e.getMessage());
        }
    }

    private static void deleteBook() {
        out.println("Enter the Id to delete:");
        out.print(">> ");
        var bookId = readLine(in);
        try {
            restClient.delete("/book/" + bookId);
            out.println("Book deleted successfully.");
        } catch (RequestException e) {
            out.println("Error deleting book: " + e.getMessage());
        }
    }

    private static void manageBookDetails() {
        var command = "";
        do {
            showBookDetailSubMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> listBookDetails();
                case "2" -> viewBookDetailById();
                case "3" -> createBookDetail();
                case "4" -> updateBookDetail();
                case "5" -> deleteBookDetail();
            }

        } while (!command.equals("exit"));
    }

    private static void showBookDetailSubMenu() {
        out.println("\nBook Detail Menu");
        out.println("1. List all Book Details");
        out.println("2. View Book Detail by ID");
        out.println("3. Create a new Book Detail");
        out.println("4. Update a Book Detail");
        out.println("5. Delete a Book Detail");
        out.println("Type 'exit' to go back.");
        out.print(">> ");
    }

    private static void listBookDetails() {
        try {
            var bookDetails = restClient.getAll("/bookDetail", BookDetailDto[].class);
            if (bookDetails != null && bookDetails.length > 0) {
                for (BookDetailDto bookDetail : bookDetails) {
                    out.print("-----------------------\n");
                    out.println("Description: " + bookDetail.getDescription());
                    out.println("Reviews: " + bookDetail.getReviews());
                    out.println("Book ID: " + bookDetail.getId());
                }
            } else {
                out.println("No book details found.");
            }
            out.print("-----------------------\n");
        } catch (RequestException e) {
            out.println("Error fetching book details: " + e.getMessage());
            e.printStackTrace();  // Optional: print stack trace for debugging
        }
    }



    private static void viewBookDetailById() {
        out.println("\nBook Detail ID to view");
        out.print(">> ");
        var bookDetailId = readLine(in);
        try {
            var bookDetail = restClient.get("/bookDetail/" + bookDetailId, BookDetailDto.class);
            out.print("-----------------------");
            out.println("\nBook ID: " + bookDetail.getId());
            out.println("Description: " + bookDetail.getDescription());
            out.println("Reviews: " + bookDetail.getReviews());
            out.print("-----------------------\n");
        } catch (RequestException e) {
            out.println("Error fetching book detail: " + e.getMessage());
        }
    }

    private static void createBookDetail() {
        var bookDetail = new BookDetailDto();
        out.println("\nBook Detail Description:");
        out.print(">> ");
        bookDetail.setDescription(readLine(in));
        out.println("Book Detail Reviews:");
        out.print(">> ");
        bookDetail.setReviews(readLine(in));
        out.println("Enter Book ID for the book related to this detail:");
        out.print(">> ");
        var bookId = Integer.parseInt(readLine(in));  // Assuming bookId is an integer.
        try {
            var book = restClient.get("/book/" + bookId, BookDto.class);
            bookDetail.setBook(book);
            restClient.post("/bookDetail", Mappers.get().writeValueAsString(bookDetail));
            if ((restClient.get("/bookDetail/" + bookId, BookDetailDto.class) != null)){
                out.println("That Book already has a Detail.");
            }
            else{
                out.println("Book detail created successfully.");
            }
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error creating book detail: " + e.getMessage());
        }
    }



    private static void updateBookDetail(){
        out.println("\nBook Detail ID to update");
        out.print(">> ");
        var bookDetailId = readLine(in);

        try {
            var bookDetail = restClient.get("/bookDetail/" + bookDetailId, BookDetailDto.class);
            if (bookDetail.getDescription() != null){
                out.println("Updating Book Detail: " + bookDetail.getDescription());
                out.println("New Description:");
                out.print(">> ");
                bookDetail.setDescription(readLine(in));
                out.println("New Reviews:");
                out.print(">> ");
                bookDetail.setReviews(readLine(in));
                var book = restClient.get("/book/" + bookDetailId, BookDto.class);
                bookDetail.setBook(book);

                restClient.put("/bookDetail/" + bookDetailId, Mappers.get().writeValueAsString(bookDetail));
                out.println("Book detail updated successfully.");
            }
            else{
                out.println("Book detail not found.");
            }

        } catch (RequestException | JsonProcessingException e) {
            out.println("Error updating book detail: " + e.getMessage());
        }
    }

    private static void deleteBookDetail() {

        out.println("\nEnter the Book Detail Id to delete:");
        out.print(">> ");
        var bookDetailId = readLine(in);
        try {
            var bookDetail = restClient.get("/bookDetail/" + bookDetailId, BookDetailDto.class);
            if (bookDetail.getDescription() != null){
                restClient.delete("/bookDetail/" + bookDetailId);
                out.println("Book detail deleted successfully.");
            }
            else{
                out.println("Book detail not found.");
            }

        } catch (RequestException e) {
            out.println("Error deleting book detail: " + e.getMessage());
        }
    }

        // Managing Customers
    private static void manageCustomers() {
        var command = "";
        do {
            showCustomerSubMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> listCustomers();
                case "2" -> viewCustomerById();
                case "3" -> createCustomer();
                case "4" -> updateCustomer();
                case "5" -> deleteCustomer();
            }

        } while (!command.equals("exit"));
    }

    private static void showCustomerSubMenu() {
        out.println("1. List all Customers");
        out.println("2. View Customer by ID");
        out.println("3. Create a new Customer");
        out.println("4. Update a Customer");
        out.println("5. Delete a Customer");
        out.println("Type 'exit' to go back.");
    }

    private static void listCustomers() {
        try {
            var customers = restClient.getAll("/customer", CustomerDto[].class);
            for (CustomerDto customer : customers) {
                out.println("\nCustomer ID: " + customer.getId());
                out.println("Name: " + customer.getFirstName() + " " + customer.getLastName());
            }
        } catch (RequestException e) {
            out.println("Error fetching customers: " + e.getMessage());
        }
    }

    private static void viewCustomerById() {
        var customerId = readLine(in);
        try {
            var customer = restClient.get("/customer/" + customerId, CustomerDto.class);
            out.println("\nCustomer ID: " + customer.getId());
            out.println("Name: " + customer.getFirstName() + " " + customer.getLastName());
            out.println("Email: " + customer.getEmail());
        } catch (RequestException e) {
            out.println("Error fetching customer: " + e.getMessage());
        }
    }

    private static void createCustomer() {
        var customer = new CustomerDto();
        customer.setFirstName(readLine(in));
        customer.setLastName(readLine(in));
        customer.setEmail(readLine(in));
        customer.setAddress(readLine(in));
        customer.setPhoneNumber(readLine(in));

        try {
            restClient.post("/customer", Mappers.get().writeValueAsString(customer));
            out.println("Customer created successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error creating customer: " + e.getMessage());
        }
    }

    private static void updateCustomer() {
        var customerId = readLine(in);
        try {
            var customer = restClient.get("/customer/" + customerId, CustomerDto.class);
            out.println("Updating Customer: " + customer.getFirstName() + " " + customer.getLastName());
            customer.setFirstName(readLine(in));  // Modify first name
            customer.setLastName(readLine(in));   // Modify last name
            customer.setEmail(readLine(in));      // Modify email
            customer.setAddress(readLine(in));    // Modify address
            customer.setPhoneNumber(readLine(in)); // Modify phone number

            restClient.put("/customer/" + customerId, Mappers.get().writeValueAsString(customer));
            out.println("Customer updated successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error updating customer: " + e.getMessage());
        }
    }

    private static void deleteCustomer() {
        out.println("Enter the Id to delete:");
        var customerId = readLine(in);
        try {
            restClient.delete("/customer/" + customerId);
            out.println("Customer deleted successfully.");
        } catch (RequestException e) {
            out.println("Error deleting customer: " + e.getMessage());
        }
    }

    // Managing Genres
    private static void manageGenres() {
        var command = "";
        do {
            showGenreSubMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> listGenres();
                case "2" -> viewGenreById();
                case "3" -> createGenre();
                case "4" -> updateGenre();
                case "5" -> deleteGenre();
            }

        } while (!command.equals("exit"));
    }

    private static void showGenreSubMenu() {
        out.println("1. List all Genres");
        out.println("2. View Genre by ID");
        out.println("3. Create a new Genre");
        out.println("4. Update a Genre");
        out.println("5. Delete a Genre");
        out.println("Type 'exit' to go back.");
    }

    private static void listGenres() {
        try {
            var genres = restClient.getAll("/genre", GenreDto[].class);
            for (GenreDto genre : genres) {
                out.println("\nGenre ID: " + genre.getId());
                out.println("Name: " + genre.getName());
            }
        } catch (RequestException e) {
            out.println("Error fetching genres: " + e.getMessage());
        }
    }

    private static void viewGenreById() {
        var genreId = readLine(in);
        try {
            var genre = restClient.get("/genre/" + genreId, GenreDto.class);
            out.println("\nGenre ID: " + genre.getId());
            out.println("Name: " + genre.getName());
        } catch (RequestException e) {
            out.println("Error fetching genre: " + e.getMessage());
        }
    }

    private static void createGenre() {
        var genre = new GenreDto();
        genre.setName(readLine(in));

        try {
            restClient.post("/genre", Mappers.get().writeValueAsString(genre));
            out.println("Genre created successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error creating genre: " + e.getMessage());
        }
    }

    private static void updateGenre() {
        var genreId = readLine(in);
        try {
            var genre = restClient.get("/genres/" + genreId, GenreDto.class);
            out.println("Updating Genre: " + genre.getName());
            genre.setName(readLine(in));  // Modify genre name

            restClient.put("/genres/" + genreId, Mappers.get().writeValueAsString(genre));
            out.println("Genre updated successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error updating genre: " + e.getMessage());
        }
    }

    private static void deleteGenre() {
        out.println("Enter the Id to delete:");
        var genreId = readLine(in);
        try {
            restClient.delete("/genre/" + genreId);
            out.println("Genre deleted successfully.");
        } catch (RequestException e) {
            out.println("Error deleting genre: " + e.getMessage());
        }
    }

    // Managing Loans
    private static void manageLoans() {
        var command = "";
        do {
            showLoanSubMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> listLoans();
                case "2" -> viewLoanById();
                case "3" -> createLoan();
                case "4" -> updateLoan();
                case "5" -> deleteLoan();
            }

        } while (!command.equals("exit"));
    }

    private static void showLoanSubMenu() {
        out.println("1. List all Loans");
        out.println("2. View Loan by ID");
        out.println("3. Create a new Loan");
        out.println("4. Update a Loan");
        out.println("5. Delete a Loan");
        out.println("Type 'exit' to go back.");
    }

    private static void listLoans() {
        try {
            var loans = restClient.getAll("/loan", LoanDto[].class);
            for (LoanDto loan : loans) {
                out.println("\nLoan ID: " + loan.getId());
                out.println("Book ID: " + loan.getBook().getId());
                out.println("Customer ID: " + loan.getCustomer().getId());
            }
        } catch (RequestException e) {
            out.println("Error fetching loans: " + e.getMessage());
        }
    }

    private static void viewLoanById() {
        var loanId = readLine(in);
        try {
            var loan = restClient.get("/loan/" + loanId, LoanDto.class);
            out.println("\nLoan ID: " + loan.getId());
            out.println("Book ID: " + loan.getBook().getId());
            out.println("Customer ID: " + loan.getCustomer().getId());
        } catch (RequestException e) {
            out.println("Error fetching loan: " + e.getMessage());
        }
    }

    private static void createLoan() {
        var loan = new LoanDto();
        loan.setBook(getBookFromInput());
        loan.setCustomer(getCustomerFromInput());
        loan.setLoanDate(Date.valueOf(readLine(in))); // Use proper date format

        try {
            restClient.post("/loan", Mappers.get().writeValueAsString(loan));
            out.println("Loan created successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error creating loan: " + e.getMessage());
        }
    }

    private static BookDto getBookFromInput() {
        out.println("Enter Book ID: ");
        var bookId = readLine(in);
        try {
            return restClient.get("/book/" + bookId, BookDto.class);
        } catch (RequestException e) {
            out.println("Error fetching book: " + e.getMessage());
            return null;
        }
    }

    private static CustomerDto getCustomerFromInput() {
        out.println("Enter Customer ID: ");
        var customerId = readLine(in);
        try {
            return restClient.get("/customer/" + customerId, CustomerDto.class);
        } catch (RequestException e) {
            out.println("Error fetching customer: " + e.getMessage());
            return null;
        }
    }

    private static void updateLoan() {
        var loanId = readLine(in);
        try {
            var loan = restClient.get("/loan/" + loanId, LoanDto.class);
            out.println("Updating Loan: " + loan.getId());
            loan.setLoanDate(Date.valueOf(readLine(in)));  // Modify loan date
            loan.setBook(getBookFromInput()); // Modify book
            loan.setCustomer(getCustomerFromInput()); // Modify customer

            restClient.put("/loan/" + loanId, Mappers.get().writeValueAsString(loan));
            out.println("Loan updated successfully.");
        } catch (RequestException | JsonProcessingException e) {
            out.println("Error updating loan: " + e.getMessage());
        }
    }

    private static void deleteLoan() {
        out.println("Enter the Id to delete:");
        var loanId = readLine(in);
        try {
            restClient.delete("/loan/" + loanId);
            out.println("Loan deleted successfully.");
        } catch (RequestException e) {
            out.println("Error deleting loan: " + e.getMessage());
        }
    }
}




