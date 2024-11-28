package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.domain.jdbc.models.Author;
import cat.uvic.teknos.library.domain.jdbc.models.Book;
import cat.uvic.teknos.library.domain.jdbc.models.BookDetail;
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
class JdbcBookDetailRepositoryTest {

    private final Connection connection;

    public JdbcBookDetailRepositoryTest(Connection connection) {
        this.connection = connection;
    }
    @Test
    public void shouldInsertBookDetail() {
        // Create an author
        Author author = new Author();
        author.setId(1);

        // Create a book
        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor(author);
        book.setPublicationDate(Date.valueOf("2023-01-01"));
        book.setISBN("1234567890123");
        book.setCopies(5);

        // Create a book detail
        BookDetail bookDetail = new BookDetail();
        bookDetail.setBook(book);
        bookDetail.setDescription("New Book Description");
        bookDetail.setReviews("New Book Reviews");

        // Save the book
        var bookRepository = new JdbcBookRepository(connection);
        bookRepository.save(book);

        // Ensure the book ID is assigned
        assertTrue(book.getId() > 0, "Book ID should be assigned");

        // Save the book detail
        var bookDetailRepository = new JdbcBookDetailRepository(connection);
        bookDetailRepository.save(bookDetail);

        // Assert that the book detail is inserted
        DbAssertions.assertThat(connection)
                .table("BOOK_DETAIL")
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

        BookDetail bookDetail = new BookDetail();
        bookDetail.setId(1); // Assuming bookDetail with ID 1 exists
        bookDetail.setBook(book);
        bookDetail.setDescription("Updated Description");
        bookDetail.setReviews("Updated Reviews");

        var repository = new JdbcBookDetailRepository(connection);
        repository.save(bookDetail);

        DbAssertions.assertThat(connection)
                .table("BOOK_DETAIL")
                .where("BOOK_ID", bookDetail.getId())
                .hasOneLine();
    }

    @Test
    void delete() {
        BookDetail bookDetail = new BookDetail();
        bookDetail.setId(1); // Assuming bookDetail with ID 1 exists

        var repository = new JdbcBookDetailRepository(connection);
        repository.delete(bookDetail);

        assertNull(repository.get(bookDetail.getId()));
    }

    @Test
    void get() {
        int id = 2; // Assuming bookDetail with ID 1 exists
        var repository = new JdbcBookDetailRepository(connection);

        BookDetail bookDetail = (BookDetail) repository.get(id);
        SoutBookDetail(bookDetail);
    }
    @Test
    void getAll() {
        var bookDetailRepository = new JdbcBookDetailRepository(connection);

        Set<cat.uvic.teknos.library.models.BookDetail> bookDetails = bookDetailRepository.getAll();

        assertFalse(bookDetails.isEmpty());


    }

    private void SoutBookDetail(BookDetail bookDetail) {
        System.out.println("BookDetail ID: " + bookDetail.getId());
        System.out.println("Description: " + bookDetail.getDescription());
        System.out.println("Reviews: " + bookDetail.getReviews());

        if (bookDetail.getBook() != null) {
            System.out.println("Book Title: " + bookDetail.getBook().getTitle());
            System.out.println("Book Author ID: " + bookDetail.getBook().getAuthor().getId());
            System.out.println("Book ISBN: " + bookDetail.getBook().getISBN());
        }

        System.out.println("\n\n");
    }
}
