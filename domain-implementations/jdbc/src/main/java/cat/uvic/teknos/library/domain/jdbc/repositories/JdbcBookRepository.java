package cat.uvic.teknos.library.domain.jdbc.repositories;



import cat.uvic.teknos.library.domain.jdbc.models.Book;
import cat.uvic.teknos.library.domain.jdbc.models.Author;
import cat.uvic.teknos.library.repositories.BookRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


//will try to do this one with a (ON DUPLICATED KEY) when inserting, that way there will be no need to make an update method
public class JdbcBookRepository implements BookRepository {

    private static final String INSERT_AUTHOR = "INSERT INTO AUTHOR (FIRST_NAME, LAST_NAME) VALUES (?,?)";
    private static final String INSERT_BOOK = "INSERT INTO BOOK (TITLE, AUTHOR_ID, PUBLICATION_DATE, ISBN, COPIES) VALUES (?, ?, ?, ?, ?)";
    private final Connection connection;

    public JdbcBookRepository(Connection connection){
        this.connection = connection;
    }


    @Override
    public void save(cat.uvic.teknos.library.models.Book model) {
        if(model.getId()<=0){
            insert((Book) model);
        }else{
            update((Book) model);
        }
    }


    private void saveAuthor(Author model, int id){
        if(model.getId()<=0){
            insertAuthor(model, id);
        }else{
            updateAuthor(model,id);
        }
    }

    private void insert(Book model) {
        try (var statement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS);
             var authorStatement = connection.prepareStatement(INSERT_AUTHOR, Statement.RETURN_GENERATED_KEYS)) {

            connection.setAutoCommit(false);

            if (model.getAuthor() != null && model.getAuthor().getId() <= 0) {
                authorStatement.setString(1, model.getAuthor().getFirstName());
                authorStatement.setString(2, model.getAuthor().getLastName());
                authorStatement.executeUpdate();
                var authorKeys = authorStatement.getGeneratedKeys();
                if (authorKeys.next()) {
                    model.getAuthor().setId(authorKeys.getInt(1));
                }
            }

            statement.setString(1, model.getTitle());
            statement.setInt(2, model.getAuthor().getId());
            statement.setDate(3, model.getPublicationDate());
            statement.setString(4, model.getISBN());
            statement.setInt(5, model.getCopies());
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
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
    private void update(Book model){
        try(
                var preparedStatement = connection.prepareStatement("UPDATE BOOK SET TITLE = ?, AUTHOR_ID = ?, PUBLICATION_DATE = ?, ISBN = ?, COPIES = ? WHERE BOOK_ID = ?");
        ) {
            connection.setAutoCommit(false);

            var id = model.getId();

            preparedStatement.setString(1, model.getTitle());
            preparedStatement.setInt(2, model.getAuthor().getId());
            preparedStatement.setDate(3, model.getPublicationDate());
            preparedStatement.setString(4, model.getISBN());
            preparedStatement.setInt(5, model.getCopies());
            preparedStatement.setInt(6, model.getId());
            preparedStatement.executeUpdate();


            if(model.getAuthor()!=null){
                saveAuthor((Author) model.getAuthor(), id);
            }

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void insertAuthor(Author model, int id) {
        try(
                var statement = connection.prepareStatement(INSERT_AUTHOR, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, model.getFirstName());
            statement.setString(2, model.getLastName());


            statement.executeUpdate();

            var keysAuthor = statement.getGeneratedKeys();
            if(keysAuthor.next()){
                model.setId(keysAuthor.getInt(1));
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateAuthor(Author model, int id) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE AUTHOR SET FIRST_NAME = ?, LAST_NAME = ? WHERE AUTHOR_ID = ?")) {
            statement.setString(1, model.getFirstName());
            statement.setString(2, model.getLastName());
            statement.setInt(3, model.getId()); // Corrected index from 6 to 3
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void delete(cat.uvic.teknos.library.models.Book model) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteDetailStatement = connection.prepareStatement("DELETE FROM BOOK_DETAIL WHERE BOOK_ID = ?");
                 PreparedStatement deleteBookStatement = connection.prepareStatement("DELETE FROM BOOK WHERE BOOK_ID = ?")) {

                deleteDetailStatement.setInt(1, model.getId());
                deleteDetailStatement.executeUpdate();

                deleteBookStatement.setInt(1, model.getId());
                deleteBookStatement.executeUpdate();

                connection.commit();
            }
        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Book get(Integer id) {
        String query = "SELECT * FROM BOOK WHERE BOOK_ID = ?";
        String query2 = "SELECT * FROM AUTHOR WHERE AUTHOR_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement authorStatement = connection.prepareStatement(query2)) {
            statement.setInt(1, id);
            Book model = new Book();

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    model.setId(resultSet.getInt("BOOK_ID"));
                    model.setTitle(resultSet.getString("TITLE"));
                    int authorId = resultSet.getInt("AUTHOR_ID");
                    model.setPublicationDate(resultSet.getDate("PUBLICATION_DATE"));
                    model.setISBN(resultSet.getString("ISBN"));
                    model.setCopies(resultSet.getInt("COPIES"));

                    authorStatement.setInt(1, authorId);
                    try (ResultSet authorResultSet = authorStatement.executeQuery()) {
                        if (authorResultSet.next()) {
                            Author author = new Author();
                            author.setId(authorResultSet.getInt("AUTHOR_ID"));
                            author.setFirstName(authorResultSet.getString("FIRST_NAME"));
                            author.setLastName(authorResultSet.getString("LAST_NAME"));
                            model.setAuthor(author);
                        }
                    }
                    return model;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    //check getAll

    @Override
    public Set<cat.uvic.teknos.library.models.Book> getAll() {
        String query = "SELECT * FROM BOOK";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                HashSet<cat.uvic.teknos.library.models.Book> books = new HashSet<>();

                while (resultSet.next()) {
                    Book result = new Book();
                    result.setId(resultSet.getInt("BOOK_ID"));
                    result.setTitle(resultSet.getString("TITLE"));
                    int authorId = resultSet.getInt("AUTHOR_ID");
                    result.setPublicationDate(resultSet.getDate("PUBLICATION_DATE"));
                    result.setISBN(resultSet.getString("ISBN"));
                    result.setCopies(resultSet.getInt("COPIES"));

                    Author author = new Author();
                    author.setId(authorId);
                    // You may need another query here to get the full author details
                    result.setAuthor(author);

                    books.add(result);
                }
                return books;
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