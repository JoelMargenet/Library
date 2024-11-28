package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.domain.jdbc.models.Author;
import cat.uvic.teknos.library.domain.jdbc.models.Book;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcBookRepositoryTest {

    private final Connection connection;

    public JdbcBookRepositoryTest(Connection connection) {
        this.connection = connection;
    }
    @Test
    void shouldInsertBook() {
        Author author = new Author();
        author.setId(1); // Assuming author with ID 1 exists

        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor(author);
        book.setPublicationDate(Date.valueOf("2023-01-01"));
        book.setISBN("1234567890123");
        book.setCopies(5);


        var bookRepository = new JdbcBookRepository(connection);
        bookRepository.save(book);

        assertTrue(book.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("BOOK")
                .where("BOOK_ID", book.getId())
                .hasOneLine();
    }

    @Test
    void shouldUpdateBookDetail() {
        Author author = new Author();
        author.setId(1); // Assuming author with ID 1 exists
        Book book = new Book();
        book.setId(1); // Assuming book with ID 1 exists
        book.setTitle("Updated Book");
        book.setAuthor(author);
        book.setPublicationDate(Date.valueOf("2023-01-01"));
        book.setISBN("0987654321098");
        book.setCopies(10);


        var repository = new JdbcBookRepository(connection);
        repository.save(book);

        DbAssertions.assertThat(connection)
                .table("BOOK")
                .where("BOOK_ID", book.getId())
                .hasOneLine();
    }

    @Test
    void delete() {
        Book book = new Book();
        book.setId(1); // Assuming bookDetail with ID 1 exists

        var repository = new JdbcBookRepository(connection);
        repository.delete(book);

        assertNull(repository.get(book.getId()));
    }

    @Test
    void get() {
        int id = 2; // Assuming bookDetail with ID 1 exists
        var repository = new JdbcBookRepository(connection);

        Book book = (Book) repository.get(id);
        SoutBook(book);
    }
    @Test
    void getAll() {
        var bookRepository = new JdbcBookRepository(connection);

        Set<cat.uvic.teknos.library.models.Book> book = bookRepository.getAll();

        assertFalse(book.isEmpty());


    }

    private void SoutBook(Book book) {
        System.out.println("Book id: " + book.getId());
        System.out.println("Title: " + book.getTitle());
        System.out.println("Author id: " + book.getAuthor().getId());
        System.out.println("Publication date: " + book.getPublicationDate());
        System.out.println("ISBN: " + book.getISBN());
        System.out.println("Copies: " + book.getCopies());

        System.out.println("\n\n");
    }
}
