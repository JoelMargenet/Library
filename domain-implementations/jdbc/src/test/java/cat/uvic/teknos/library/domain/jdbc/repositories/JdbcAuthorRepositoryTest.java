package cat.uvic.teknos.library.domain.jdbc.repositories;

import cat.uvic.teknos.library.domain.jdbc.models.Author;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcAuthorRepositoryTest {

    private final Connection connection;

    public JdbcAuthorRepositoryTest(Connection connection){
        this.connection = connection;
    }


    @Test
    void shouldInsertAuthor() {

        Author author = new Author();
        author.setFirstName("Ivan");
        author.setLastName("Vidal");


        var repository = new JdbcAuthorRepository(connection);
        repository.save(author);

        assertTrue(author.getId() > 0);


        DbAssertions.assertThat(connection)
                .table("AUTHOR")
                .where("AUTHOR_ID", author.getId())
                .hasOneLine();

    }

    @Test
    void shouldUpdateAuthor(){

        Author author = new Author();
        author.setId(3);
        author.setFirstName("Ivan");
        author.setLastName("Vidal");



        var repository = new JdbcAuthorRepository(connection);
        repository.save(author);


    }

    @Test
    void delete() {

        Author author = new Author();
        author.setId(1);


        var repository = new JdbcAuthorRepository(connection);

        repository.delete(author);

    }

    @Test
    void get() {
        int id = 1;
        var repository = new JdbcAuthorRepository(connection);

        cat.uvic.teknos.library.models.Author author = repository.get(id);
        SoutAuthor(author);
    }

    @Test
    void getAll() {
        var repository = new JdbcAuthorRepository(connection);
        Set<cat.uvic.teknos.library.models.Author> authors = repository.getAll();

        for(var author:authors){
            SoutAuthor(author);
        }
    }

    private void SoutAuthor(cat.uvic.teknos.library.models.Author author){
        System.out.println("Author: " + author.getId());
        System.out.println("Name: " + author.getFirstName());
        System.out.println("Last Name: " + author.getLastName());



        System.out.println("\n\n");
    }

}