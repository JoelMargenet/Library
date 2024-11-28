package cat.uvic.teknos.library.backoffice;

import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.BookDetail;
import cat.uvic.teknos.library.models.ModelFactory;
import cat.uvic.teknos.library.repositories.BookDetailRepository;
import cat.uvic.teknos.library.repositories.BookRepository;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.BufferedReader;
import java.io.PrintStream;

import static cat.uvic.teknos.library.backoffice.IOUtils.readLine;

public class BookDetailManager {
    private final BufferedReader in;
    private final PrintStream out;
    private final BookDetailRepository bookDetailRepository;
    private final BookRepository bookRepository;
    private final ModelFactory modelFactory;

    public BookDetailManager(BufferedReader in, PrintStream out, BookDetailRepository bookDetailRepository, ModelFactory modelFactory, BookRepository bookRepository) {
        this.in = in;
        this.out = out;
        this.bookDetailRepository = bookDetailRepository;
        this.bookRepository = bookRepository;
        this.modelFactory = modelFactory;
    }

    public void start() {
        out.println("Book Detail:");
        out.println("1 to insert new Book Detail");
        out.println("2 to update Book Detail");
        out.println("3 to show all Book Details");
        out.println("4 to get Book Detail by Book ID");
        out.println("5 to delete Book Detail by Book ID");
        out.println("Write exit to go to the main menu \n");

        var command = "";
        do {
            command = readLine(in);

            try {
                switch (command) {
                    case "1" -> insert();
                    case "2" -> update();
                    case "3" -> getAll();
                    case "4" -> getById();
                    case "5" -> deleteById();
                }
            } catch (Exception e) {
                out.println("An error occurred: " + e.getMessage());
                e.printStackTrace(out);
            }

        } while (!command.equals("exit"));

        out.println("Bye!");
    }

    private void insert() {
        out.println("Insert Book Detail:");
        out.println("Enter the Book ID for the new book detail:");
        int bookId = Integer.parseInt(readLine(in));

        var book = bookRepository.get(bookId);
        if (book == null) {
            out.println("Book not found with ID: " + bookId);
            return;
        }

        if (bookDetailRepository.get(bookId) != null) {
            out.println("Book detail already exists for Book ID: " + bookId);
            return;
        }

        var bookDetail = modelFactory.createBookDetail();
        bookDetail.setBook(book); // Set the Book reference in BookDetail

        out.println("Description:");
        bookDetail.setDescription(readLine(in));

        out.println("Enter a review:");
        bookDetail.setReviews(readLine(in));

        bookDetailRepository.save(bookDetail);

        out.println("Inserted book detail successfully: " + bookDetail);

        out.println();
        start();
    }

    private void update() {
        out.println("Update Book Detail:");
        out.println("Enter the Book ID of the book detail you want to update:");
        int bookDetailId = Integer.parseInt(readLine(in));

        var bookDetail = bookDetailRepository.get(bookDetailId);
        if (bookDetail == null) {
            out.println("Book detail not found with Book ID: " + bookDetailId);
            return;
        }

        var book = bookRepository.get(bookDetailId);
        if (book == null) {
            out.println("Book not found with ID: " + bookDetailId);
            return;
        }

        // Ensure the BookDetail has its associated Book set
        bookDetail.setBook(book);

        out.println("Enter the new description:");
        bookDetail.setDescription(readLine(in));

        out.println("Enter a review:");
        bookDetail.setReviews(readLine(in));

        bookDetailRepository.save(bookDetail);

        out.println("Updated book detail successfully: " + bookDetail);

        out.println();
        start();
    }

    private void getAll() {
        var asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("Book ID", "Description", "Reviews");
        asciiTable.addRule();

        for (var bookDetail : bookDetailRepository.getAll()) {
            asciiTable.addRow(bookDetail.getId(), bookDetail.getDescription(), String.join(", ", bookDetail.getReviews()));
            asciiTable.addRule();
        }

        asciiTable.setTextAlignment(TextAlignment.CENTER);

        String render = asciiTable.render();
        out.println(render);

        out.println();
        start();
    }
    private void getById() {
        out.println("Enter Book ID of the book detail to retrieve:");
        int id = Integer.parseInt(readLine(in));
        BookDetail bookDetail = bookDetailRepository.get(id);
        if (bookDetail != null) {
            Book book = bookDetail.getBook();
            if (book != null) {
                out.println("Book detail found: " + book.getId() + "Title: " + book.getTitle());
            } else {
                out.println("Book detail found, but the associated Book is null.");
            }
            out.println("Description: " + bookDetail.getDescription());
            out.println("Reviews: " + bookDetail.getReviews());
        } else {
            out.println("Book detail not found with Book ID: " + id);
        }

        out.println();
        start();
    }

    private void deleteById() {
        out.println("Enter Book ID of the book detail to delete:");
        int id = Integer.parseInt(readLine(in));
        BookDetail bookDetail = bookDetailRepository.get(id);
        if (bookDetail != null) {
            bookDetailRepository.delete(bookDetail);
            out.println("Book detail deleted successfully with Book ID: " + id);
        } else {
            out.println("Book detail not found with Book ID: " + id);
        }

        out.println();
        start();
    }
}