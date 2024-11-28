package cat.uvic.teknos.library.backoffice;

import cat.uvic.teknos.library.models.ModelFactory;
import cat.uvic.teknos.library.repositories.RepositoryFactory;

import static cat.uvic.teknos.library.backoffice.IOUtils.*;

import java.io.*;

public class BackOffice {
    private final BufferedReader in;
    private final PrintStream out;
    private final RepositoryFactory repositoryFactory;
    private final ModelFactory modelFactory;

    public BackOffice (InputStream inputStream, OutputStream outputStream, RepositoryFactory repositoryFactory, ModelFactory modelFactory) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        this.out = new PrintStream(outputStream);
        this.repositoryFactory = repositoryFactory;
        this.modelFactory = modelFactory;

    }
    public void showWelcomeMessage() {
        out.println("welcome to the library Back Office");
        out.println("Select a menu option or type exit to exit the application");
    }

    private void showMainMenu() {
        out.println("1: Author");
        out.println("2: Book");
        out.println("3: Book Detail");
        out.println("4: Customer");
        out.println("5: Genre");
        out.println("6: Loan");
        out.println("Write exit for go to the main menu \n");

    }

    public void start() {
        showWelcomeMessage();

       var command = "";
       do {
           showMainMenu();
           command = readLine(in);
           switch (command) {

               case "1" -> manageAuthors();
               case "2" -> manageBooks();
               case "3" -> manageBookDetail();
               case "4" -> manageCustomers();
               case "5" -> manageGenres();
               case "6" -> manageLoans();

           }

        } while (!command.equals("exit"));

        out.println("Bye!");
      }


    private void manageAuthors() {
        new AuthorsManager(in, out, repositoryFactory.getAuthorRepository(), modelFactory).start();
    }
    private void manageBooks() {
        new BooksManager(in, out, repositoryFactory.getBookRepository(), modelFactory).start();
    }
    private void manageBookDetail() {
        new BookDetailManager(in, out, repositoryFactory.getBookDetailRepository(), modelFactory, repositoryFactory.getBookRepository()).start();
    }
    private void manageCustomers() {
        new CustomersManager(in, out, repositoryFactory.getCustomerRepository(), modelFactory).start();
    }
    private void manageGenres() {
        new GenresManager(in, out, repositoryFactory.getGenreRepository(), modelFactory).start();
    }
    private void manageLoans() {
        new LoansManager(in, out, repositoryFactory.getLoanRepository(), repositoryFactory.getCustomerRepository(),
                repositoryFactory.getBookRepository()).start();
    }

}