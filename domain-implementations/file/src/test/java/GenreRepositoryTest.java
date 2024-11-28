import cat.uvic.teknos.library.file.models.Genre;
import org.junit.jupiter.api.Test;
import cat.uvic.teknos.library.file.repositories.GenreRepository;

import static org.junit.jupiter.api.Assertions.*;

class GenreRepositoryTest {

    @Test
    void save() {
        var repository = new GenreRepository();
        var genre = new Genre();
        genre.setId(1);
        genre.setName("Fiction");
        repository.save(genre);
        assertTrue(genre.getId() > 0);
        assertNotNull(repository.get(genre.getId()));
    }

    @Test
    void update() {
        var repository = new GenreRepository();
        var genre = new Genre();
        genre.setId(1);
        genre.setName("Mystery");
        repository.save(genre);

        var updatedGenre = repository.get(1);
        updatedGenre.setName("Thriller");
        repository.save(updatedGenre);

        var retrievedGenre = repository.get(1);
        assertEquals("Thriller", retrievedGenre.getName());
    }

    @Test
    void delete() {
        var repository = new GenreRepository();
        var genre = new Genre();
        repository.delete(genre);
        assertNull(repository.get(genre.getId()));
    }

    @Test
    void get() {
        var repository = new GenreRepository();
        var genre = new Genre();
        genre.setId(1);
        genre.setName("Fiction");
        repository.save(genre);

        var retrievedGenre = repository.get(1);
        assertNotNull(retrievedGenre);
        assertEquals("Fiction", retrievedGenre.getName());
    }

    @Test
    void getAll() {
        var repository = new GenreRepository();
        var genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Fiction");
        repository.save(genre1);

        var genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Mystery");
        repository.save(genre2);

        var genres = repository.getAll();
        assertEquals(2, genres.size());
        assertTrue(genres.contains(genre1));
        assertTrue(genres.contains(genre2));
    }
}