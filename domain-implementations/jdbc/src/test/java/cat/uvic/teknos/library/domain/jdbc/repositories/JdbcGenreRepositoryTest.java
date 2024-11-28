package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.domain.jdbc.models.Genre;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcGenreRepositoryTest {

    private final Connection connection;

    public JdbcGenreRepositoryTest(Connection connection) {
        this.connection = connection;
    }

    @Test
    void shouldInsertGenre() {
        cat.uvic.teknos.library.models.Genre genre = new Genre();
        genre.setName("Science Fiction");

        var repository = new JdbcGenreRepository(connection);
        repository.save(genre);

        assertTrue(genre.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .where("GENRE_ID", genre.getId())
                .hasOneLine();
    }

    @Test
    void shouldUpdateGenre() {
        cat.uvic.teknos.library.models.Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Updated Genre Name");

        var repository = new JdbcGenreRepository(connection);
        repository.save(genre);

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .where("GENRE_ID", genre.getId())
                .hasOneLine();
    }

    @Test
    void delete() {
        cat.uvic.teknos.library.models.Genre genre = new Genre();
        genre.setId(1);

        var repository = new JdbcGenreRepository(connection);
        repository.delete(genre);

        assertNull(repository.get(genre.getId()));
    }

    @Test
    void get() {
        int id = 2;

        var repository = new JdbcGenreRepository(connection);
        cat.uvic.teknos.library.models.Genre genre = repository.get(id);
        SoutGenre(genre);
    }

    @Test
    void getAll() {
        var repository = new JdbcGenreRepository(connection);
        Set<cat.uvic.teknos.library.models.Genre> genres = repository.getAll();

        for(var genre:genres){
            SoutGenre(genre);
        }
    }

    private void SoutGenre(cat.uvic.teknos.library.models.Genre genre){
        System.out.println("Genre: " + genre.getId());
        System.out.println("Name: " + genre.getName());

        System.out.println("\n\n");
    }
}