import cat.uvic.teknos.library.file.models.BookDetail;
import org.junit.jupiter.api.Test;
import cat.uvic.teknos.library.file.repositories.BookDetailRepository;

import static org.junit.jupiter.api.Assertions.*;

class BookDetailRepositoryTest {

    @Test
    void save() {
        var repository = new BookDetailRepository();
        var bookDetail = new BookDetail();
        bookDetail.setId(1);
        bookDetail.setReviews("Good");
        // Set other properties of the book details object

        repository.save(bookDetail);
        assertTrue(bookDetail.getId() > 0);
        assertNotNull(repository.get(bookDetail.getId()));
    }

    @Test
    void update() {
        var repository = new BookDetailRepository();
        var bookDetail = new BookDetail();
        bookDetail.setId(1);
        bookDetail.setReviews("Good");
        // Set other properties of the book details object

        repository.save(bookDetail);

        var updatedBookDetail = repository.get(1);
        // Modify some properties of the updated book details object
        repository.save(updatedBookDetail);

        var retrievedBookDetail = repository.get(1);
        // Verify properties of the retrieved book details object
    }

    @Test
    void delete() {
        var repository = new BookDetailRepository();
        var bookDetail = new BookDetail();
        repository.delete(bookDetail);
        assertNull(repository.get(bookDetail.getId()));
    }

    @Test
    void get() {
        var repository = new BookDetailRepository();
        var bookDetail = new BookDetail();
        bookDetail.setId(1);
        bookDetail.setReviews("Good");
        // Set other properties of the book details object

        repository.save(bookDetail);

        var retrievedBookDetail = repository.get(1);
        assertNotNull(retrievedBookDetail);
        assertEquals("Good", retrievedBookDetail.getReviews());
        // Verify other properties of the retrieved book details object
    }

    @Test
    void getAll() {
        var repository = new BookDetailRepository();
        var bookDetail1 = new BookDetail();
        bookDetail1.setId(1);
        bookDetail1.setReviews("Good");
        // Set other properties of the book details object

        repository.save(bookDetail1);

        var bookDetail2 = new BookDetail();
        bookDetail2.setId(2);
        bookDetail2.setReviews("Good");
        // Set other properties of the book details object

        repository.save(bookDetail2);

        var allBookDetail = repository.getAll();
        assertEquals(2, allBookDetail.size());
    }
}