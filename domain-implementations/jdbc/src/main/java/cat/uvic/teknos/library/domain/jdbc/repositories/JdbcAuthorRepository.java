package cat.uvic.teknos.library.domain.jdbc.repositories;


import cat.uvic.teknos.library.domain.jdbc.models.Author;
import cat.uvic.teknos.library.repositories.AuthorRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


//will try to do this one with a (ON DUPLICATED KEY) when inserting, that way there will be no need to make an update method
public class JdbcAuthorRepository implements AuthorRepository {

    private static final String INSERT_AUTHOR = "INSERT INTO AUTHOR (FIRST_NAME, LAST_NAME) VALUES (?,?)";
        private final Connection connection;

    public JdbcAuthorRepository(Connection connection){
        this.connection = connection;
    }



    @Override
    public void save(cat.uvic.teknos.library.models.Author model) {
        if(model.getId()<=0){
            insert(model);
        }else{
            update(model);
        }
    }


    //INSERT AND UPDATE FOR AUTHOR

    private void insert(cat.uvic.teknos.library.models.Author model) {
        try(var statement = connection.prepareStatement(INSERT_AUTHOR,Statement.RETURN_GENERATED_KEYS);
        ){


            connection.setAutoCommit(false);


            statement.setString(1, model.getFirstName());
            statement.setString(2, model.getLastName());

            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if(keys.next()) {
                model.setId(keys.getInt(1));
            }

            connection.commit();

        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    private void update(cat.uvic.teknos.library.models.Author model){
        try(
                var preparedStatement = connection.prepareStatement("UPDATE AUTHOR SET FIRST_NAME = ?, LAST_NAME = ? WHERE AUTHOR_ID = ?");
        ) {
            connection.setAutoCommit(false);

            var id = model.getId();

            preparedStatement.setString(1, model.getFirstName());
            preparedStatement.setString(2, model.getLastName());
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(cat.uvic.teknos.library.models.Author model) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM AUTHOR where AUTHOR_ID = ?");

        ) {

            connection.setAutoCommit(false);

            preparedStatement.setInt(1,model.getId());

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Author get(Integer id) {
        String query = "SELECT * FROM AUTHOR WHERE AUTHOR_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setInt(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cat.uvic.teknos.library.models.Author result = new Author();
                    result.setId(resultSet.getInt("AUTHOR_ID"));
                    result.setFirstName(resultSet.getString("FIRST_NAME"));
                    result.setLastName(resultSet.getString("LAST_NAME"));

                    return result;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    //check getAll
    @Override
    public Set<cat.uvic.teknos.library.models.Author> getAll() {
        String query = "SELECT * FROM AUTHOR";


        try (PreparedStatement statement = connection.prepareStatement(query);
        ) {

            int id = 1;
            try (var resultSet = statement.executeQuery()) {

                var authors = new HashSet<cat.uvic.teknos.library.models.Author>();

                while (resultSet.next()) {

                    id++;
                    cat.uvic.teknos.library.models.Author result = new Author();
                    result.setId(resultSet.getInt("AUTHOR_ID"));
                    result.setFirstName(resultSet.getString("FIRST_NAME"));
                    result.setLastName(resultSet.getString("LAST_NAME"));


                    authors.add(result);

                }
                return authors;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void setAutoCommitTrue() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void rollback() {
        try{
            connection.rollback();
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}