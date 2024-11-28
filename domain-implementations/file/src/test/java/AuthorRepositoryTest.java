import cat.uvic.teknos.library.file.models.Author;
import org.junit.jupiter.api.Test;
import cat.uvic.teknos.library.file.repositories.AuthorRepository;

import static org.junit.jupiter.api.Assertions.*;

class AuthorRepositoryTest {

    @Test
    void save() {
        var repository = new AuthorRepository();
        var author = new Author();
        author.setId(1);
        author.setFirstName("Joel");
        author.setLastName("Margenet");
        repository.save(author);
        assertTrue(author.getId() > 0);
        assertNotNull(repository.get(author.getId()));
    }

    @Test
    void update() {
        var repository = new AuthorRepository();
        var author = new Author();
        author.setId(1);
        author.setFirstName("Pau");
        author.setLastName("Sala");
        repository.save(author);

        var updatedAuthor = repository.get(1);
        updatedAuthor.setFirstName("Updated Name");
        repository.save(updatedAuthor);

        var retrievedAuthor = repository.get(1);
        assertEquals("Updated Name", retrievedAuthor.getFirstName());
    }

    @Test
    void delete() {
        var repository = new AuthorRepository();
        var author = new Author();
        repository.delete(author);
        assertNull(repository.get(author.getId()));
    }

    @Test
    void get() {
        var repository = new AuthorRepository();
        var author = new Author();
        author.setId(1);
        author.setFirstName("John");
        author.setLastName("Doe");
        repository.save(author);

        var retrievedAuthor = repository.get(1);
        assertNotNull(retrievedAuthor);
        assertEquals("John", retrievedAuthor.getFirstName());
        assertEquals("Doe", retrievedAuthor.getLastName());
    }

    @Test
    void getAll() {
        var repository = new AuthorRepository();
        var author1 = new Author();
        author1.setId(1);
        author1.setFirstName("Jane");
        author1.setLastName("Smith");
        repository.save(author1);

        var author2 = new Author();
        author2.setId(2);
        author2.setFirstName("Alice");
        author2.setLastName("Johnson");
        repository.save(author2);

        var allAuthors = repository.getAll();
        assertNotNull(allAuthors);
        assertEquals(2, allAuthors.size());
    }
}