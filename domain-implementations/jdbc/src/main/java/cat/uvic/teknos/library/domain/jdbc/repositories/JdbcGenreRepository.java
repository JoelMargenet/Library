package cat.uvic.teknos.library.domain.jdbc.repositories;


import cat.uvic.teknos.library.domain.jdbc.models.Genre;
import cat.uvic.teknos.library.repositories.GenreRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


//will try to do this one with a (ON DUPLICATED KEY) when inserting, that way there will be no need to make an update method
public class JdbcGenreRepository implements GenreRepository {

    private static final String DELETE_GENRE = "DELETE FROM GENRE WHERE GENRE_ID = ?";
    private static final String DELETE_BOOK_GENRE = "DELETE FROM BOOK_GENRE WHERE GENRE_ID = ?";
    private static final String INSERT_GENRE = "INSERT INTO GENRE (Name) VALUES (?)";
    private final Connection connection;

    public JdbcGenreRepository(Connection connection){
        this.connection = connection;
    }


    @Override
    public void save(cat.uvic.teknos.library.models.Genre model) {
        if(model.getId()<=0){
            insert(model);
        }else{
            update(model);
        }
    }




    private void insert(cat.uvic.teknos.library.models.Genre model) {
        try(var statement = connection.prepareStatement(INSERT_GENRE,Statement.RETURN_GENERATED_KEYS);
        ){


            connection.setAutoCommit(false);

            statement.setString(1, model.getName());

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

    private void update(cat.uvic.teknos.library.models.Genre model){
        try(
                var preparedStatement = connection.prepareStatement("UPDATE GENRE SET NAME = ? WHERE GENRE_ID = ?");
        ) {
            connection.setAutoCommit(false);

            var id = model.getId();

            preparedStatement.setString(1, model.getName());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(cat.uvic.teknos.library.models.Genre model) {
        try (PreparedStatement deleteBookGenreStatement = connection.prepareStatement(DELETE_BOOK_GENRE);
             PreparedStatement deleteGenreStatement = connection.prepareStatement(DELETE_GENRE)) {
            int genreId = model.getId();
            connection.setAutoCommit(false);

            // Delete entries from the BOOK_GENRE table related to the genre
            deleteBookGenreStatement.setInt(1, genreId);
            deleteBookGenreStatement.executeUpdate();

            // Now, delete the genre itself
            deleteGenreStatement.setInt(1, genreId);
            deleteGenreStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Genre get(Integer id) {
        String query = "SELECT * FROM GENRE WHERE GENRE_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setInt(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cat.uvic.teknos.library.models.Genre result = new Genre();
                    result.setId(resultSet.getInt("GENRE_ID"));
                    result.setName(resultSet.getString("NAME"));

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
    public Set<cat.uvic.teknos.library.models.Genre> getAll() {
        String query = "SELECT * FROM GENRE";


        try (PreparedStatement statement = connection.prepareStatement(query);
        ) {

            int id = 1;
            try (var resultSet = statement.executeQuery()) {

                var genres = new HashSet<cat.uvic.teknos.library.models.Genre>();

                while (resultSet.next()) {

                    id++;
                    cat.uvic.teknos.library.models.Genre result = new Genre();
                    result.setId(resultSet.getInt("GENRE_ID"));
                    result.setName(resultSet.getString("NAME"));


                    genres.add(result);

                }
                return genres;
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