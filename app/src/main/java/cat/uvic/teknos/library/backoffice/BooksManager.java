package cat.uvic.teknos.library.backoffice;

import cat.uvic.teknos.library.models.Author;
import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.ModelFactory;
import cat.uvic.teknos.library.repositories.BookRepository;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.sql.Date;
import java.time.LocalDate;

import static cat.uvic.teknos.library.backoffice.IOUtils.readLine;

public class BooksManager {
    private final BufferedReader in;
    private final PrintStream out;
    private final BookRepository bookRepository;
    private final ModelFactory modelFactory;

    public BooksManager(BufferedReader in, PrintStream out, BookRepository bookRepository, ModelFactory modelFactory) {
        this.in = in;
        this.out = out;
        this.bookRepository = bookRepository;
        this.modelFactory = modelFactory;
    }

    public void start() {
        out.println("Books:");
        out.println("1 to insert a new Book");
        out.println("2 to update a Book");
        out.println("3 to show all Books");
        out.println("4 to get a Book by ID");
        out.println("5 to delete a Book by ID");
        out.println("Write exit for go to the main menu \n");

        String command;
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
        var book = modelFactory.createBook();

        out.println("Title:");
        book.setTitle(readLine(in));

        out.println("Author ID:");
        int authorId = Integer.parseInt(readLine(in));


        Author author = modelFactory.createAuthor();
        author.setId(authorId);
        book.setAuthor(author);

        out.println("Publication date (yyyy-mm-dd):");
        Date publicationDate = Date.valueOf(LocalDate.parse(readLine(in)));
        book.setPublicationDate(publicationDate);

        out.println("ISBN:");
        book.setISBN(readLine(in));

        out.println("Number of copies:");
        int copies = Integer.parseInt(readLine(in));
        book.setCopies(copies);

        // Perform the insert operation
        try {
            bookRepository.save(book);
            out.println("Inserted book successfully: " + book);
        } catch (Exception e) {
            out.println("Failed to insert book: " + e.getMessage());
            e.printStackTrace(out); // Print the stack trace for debugging
        }
        out.println();
        start();
    }

    private void update() {
        out.println("Enter ID of the book to update:");
        int id = Integer.parseInt(readLine(in));
        Book book = bookRepository.get(id);
        if (book != null) {
            out.println("Enter new title:");
            book.setTitle(readLine(in));

            out.println("Enter new author ID:");
            int authorId = Integer.parseInt(readLine(in));
            // Assuming book.getAuthor() returns a valid author object
            book.getAuthor().setId(authorId);

            out.println("Enter new publication date (yyyy-mm-dd):");
            Date publicationDate = Date.valueOf(LocalDate.parse(readLine(in)));
            book.setPublicationDate(publicationDate);

            out.println("Enter new ISBN:");
            book.setISBN(readLine(in));

            out.println("Enter new number of copies:");
            int copies = Integer.parseInt(readLine(in));
            book.setCopies(copies);

            // Save the updated book
            try {
                bookRepository.save(book);
                out.println("Book updated successfully: " + book);
            } catch (Exception e) {
                out.println("Failed to update book: " + e.getMessage());
                e.printStackTrace(out);
            }
        } else {
            out.println("Book not found with ID: " + id);
        }
        out.println();
        start();
    }
    private void getAll() {
        var asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("Id", "Title", "Author_id", "Publication_date", "ISBN", "Copies");
        asciiTable.addRule();

        for (var book : bookRepository.getAll()) {
            asciiTable.addRow(book.getId(), book.getTitle(), book.getAuthor().getId(), book.getPublicationDate(), book.getISBN(), book.getCopies());
            asciiTable.addRule();
        }

        asciiTable.setTextAlignment(TextAlignment.CENTER);

        String render = asciiTable.render();
        System.out.println(render);

        out.println();
        start();
    }
    private void getById() {
        out.println("Enter ID of the book to retrieve:");
        int id = Integer.parseInt(readLine(in));
        Book book = bookRepository.get(id);
        if (book != null) {
            out.println("Title: " + book.getTitle());
            out.println("Author: " + book.getAuthor().getId() + " " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
            out.println("Publication date: " + book.getPublicationDate());
            out.println("ISBN: " + book.getISBN());
            out.println("Copies: " + book.getCopies());

        } else {
            out.println("Book not found with ID: " + id);
        }
        out.println();
        start();
    }

    private void deleteById() {
        out.println("Enter ID of the book to delete:");
        int id = Integer.parseInt(readLine(in));
        Book book = bookRepository.get(id);
        if (book != null) {
            bookRepository.delete(book);
            out.println("Book deleted successfully with ID: " + id);
        } else {
            out.println("Book not found with ID:" + id);
        }
        out.println();
        start();
    }
}