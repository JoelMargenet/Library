package cat.uvic.teknos.library.backoffice;

import cat.uvic.teknos.library.models.ModelFactory;
import cat.uvic.teknos.library.models.Author;
import cat.uvic.teknos.library.repositories.AuthorRepository;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.BufferedReader;
import java.io.PrintStream;

import static cat.uvic.teknos.library.backoffice.IOUtils.readLine;

public class AuthorsManager {
    private final BufferedReader in;
    private final PrintStream out;
    private final AuthorRepository authorRepository;
    private final ModelFactory modelFactory;

    public AuthorsManager(BufferedReader in, PrintStream out, AuthorRepository authorRepository, ModelFactory modelFactory) {
        this.in = in;
        this.out = out;
        this.authorRepository = authorRepository;
        this.modelFactory = modelFactory;
    }

    public void start() {
        out.println("Authors:");
        out.println("1 to insert a new Author");
        out.println("2 to update an Author");
        out.println("3 to show all Authors");
        out.println("4 to get an Author by ID");
        out.println("5 to delete an Author by ID");
        out.println("Write exit to go to the main menu \n");

        var command = "";
        do {
            command = readLine(in);

            switch (command) {
                case "1" -> insert();
                case "2" -> update();
                case "3" -> getAll();
                case "4" -> getById();
                case "5" -> deleteById();
            }

        } while (!command.equals("exit"));

        out.println("Bye!");
    }

    private void insert() {
        var author = modelFactory.createAuthor();

        out.println("First name:");
        author.setFirstName(readLine(in));

        out.println("Last name:");
        author.setLastName(readLine(in));

        authorRepository.save(author);

        out.println("Inserted author successfully: " + author);

        out.println();
        start();
    }

    private void update() {
        out.println("Enter ID of the author to update:");
        int id = Integer.parseInt(readLine(in));
        Author author = authorRepository.get(id);
        if (author != null) {
            out.println("Enter new first name:");
            author.setFirstName(readLine(in));
            out.println("Enter new last name:");
            author.setLastName(readLine(in));

            authorRepository.save(author);
            out.println("Author updated successfully: " + author);
        } else {
            out.println("Author not found with ID: " + id);
        }
        out.println();
        start();
    }

    private void getAll() {
        var asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("Id", "First name", "Last name");
        asciiTable.addRule();

        for (var author : authorRepository.getAll()) {
            asciiTable.addRow(author.getId(), author.getFirstName(), author.getLastName());
            asciiTable.addRule();
        }

        asciiTable.setTextAlignment(TextAlignment.CENTER);

        String render = asciiTable.render();
        System.out.println(render);

        out.println();
        start();
    }

    private void getById() {
        out.println("Enter ID of the author to retrieve:");
        int id = Integer.parseInt(readLine(in));
        Author author = authorRepository.get(id);
        if (author != null) {
            out.println("Author found: " + author.getId());
            out.println("First Name: " + author.getFirstName());
            out.println("Last Name: " + author.getLastName());
        } else {
            out.println("Author not found with ID: " + id);
        }

        out.println();
        start();
    }

    private void deleteById() {
        out.println("Enter ID of the author to delete:");
        int id = Integer.parseInt(readLine(in));
        Author author = authorRepository.get(id);
        if (author != null) {
            authorRepository.delete(author);
            out.println("Author deleted successfully with ID: " + id);
        } else {
            out.println("Author not found with ID: " + id);
        }

        out.println();
        start();
    }



}