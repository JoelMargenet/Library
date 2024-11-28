package cat.uvic.teknos.library.domain.jdbc.repositories;


import cat.uvic.teknos.library.domain.jdbc.models.BookDetail;
import cat.uvic.teknos.library.domain.jdbc.models.Book;
import cat.uvic.teknos.library.repositories.BookDetailRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


//will try to do this one with a (ON DUPLICATED KEY) when inserting, that way there will be no need to make an update method
public class JdbcBookDetailRepository implements BookDetailRepository {

    private static final String INSERT_BOOK_DETAIL = "INSERT INTO BOOK_DETAIL (BOOK_ID, DESCRIPTION, REVIEWS) VALUES (?,?,?)";
    private static final String INSERT_BOOK = "INSERT INTO BOOK (TITLE, AUTHOR_ID, PUBLICATION_DATE, ISBN, COPIES) VALUES (?, ?, ?, ?, ?)";
    private final Connection connection;

    public JdbcBookDetailRepository(Connection connection){
        this.connection = connection;
    }


    @Override
    public void save(cat.uvic.teknos.library.models.BookDetail model) {
        if(model.getId()<=0){
            insert(model);
        }else{
            update(model);
        }
    }


    private void saveBook(Book model, int id){
        if(model.getId()<=0){
            insertBook(model, id);
        }else{
            updateBook(model,id);
        }
    }


    private void insert(cat.uvic.teknos.library.models.BookDetail model) {
        try(var statement = connection.prepareStatement(INSERT_BOOK_DETAIL,Statement.RETURN_GENERATED_KEYS);
            var bookStatement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS);
        ){


            connection.setAutoCommit(false);


            statement.setInt(1, model.getBook().getId());
            statement.setString(2, model.getDescription());
            statement.setString(3, model.getReviews());

            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if(keys.next()){
                model.setId(keys.getInt(1));
            }

            //INSERT TO OTHER TABLES
            int id = model.getId();


            if(model.getBook()!=null){
                saveBook((Book) model.getBook(), id);
            }

            connection.commit();

        } catch (SQLException e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            setAutoCommitTrue();
        }
    }

    private void update(cat.uvic.teknos.library.models.BookDetail model){
        try(
                var preparedStatement = connection.prepareStatement("UPDATE BOOK_DETAIL SET BOOK_ID = ?, DESCRIPTION = ?, REVIEWS = ? WHERE BOOK_ID = ?");
        ) {
            connection.setAutoCommit(false);

            var id = model.getId();

            if(model.getDescription()!=null){
                preparedStatement.setInt(1, model.getBook().getId());
                preparedStatement.setString(2, model.getDescription());
                preparedStatement.setString(3, model.getReviews());
                preparedStatement.setInt(4, model.getBook().getId());
                preparedStatement.executeUpdate();
            }

            if(model.getBook()!=null){
                saveBook((Book) model.getBook(), id);
            }

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void insertBookDetail(cat.uvic.teknos.library.models.BookDetail model) {
        try(
                var bookDetailStatement = connection.prepareStatement(INSERT_BOOK_DETAIL);
        ) {
            bookDetailStatement.setInt(1, model.getBook().getId());
            bookDetailStatement.setString(2, model.getDescription());
            bookDetailStatement.setString(3, model.getReviews());
            bookDetailStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateBookDetail(cat.uvic.teknos.library.models.BookDetail model) {
        try(
                var statement = connection.prepareStatement("UPDATE BOOK_DETAIL SET BOOK_ID = ?, DESCRIPTION = ?, REVIEWS = ? WHERE BOOK_ID = ?")
        ){
            statement.setInt(1, model.getBook().getId());
            statement.setString(2, model.getDescription());
            statement.setString(3, model.getReviews());
            statement.setInt(4, model.getBook().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertBook(Book model, int id) {
        try(
                var statement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, model.getTitle());
            statement.setInt(2, model.getAuthor().getId());
            statement.setDate(3, model.getPublicationDate());
            statement.setString(4, model.getISBN());
            statement.setInt(5, model.getCopies());

            statement.executeUpdate();
            //testing
            var keysBook = statement.getGeneratedKeys();
            if(keysBook.next()){
                model.setId(keysBook.getInt(1));
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBook(Book model, int id) {
        try(
                var statement = connection.prepareStatement("UPDATE BOOK SET TITLE = ?, AUTHOR_ID = ?, PUBLICATION_DATE = ?, ISBN = ?, COPIES = ? WHERE BOOK_ID = ? ");

        ) {
            int idBook = model.getId();
            statement.setString(1, model.getTitle());
            statement.setInt(2, model.getAuthor().getId());
            statement.setDate(3, model.getPublicationDate());
            statement.setString(4, model.getISBN());
            statement.setInt(5, model.getCopies());
            statement.setInt(6, idBook);
            statement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(cat.uvic.teknos.library.models.BookDetail model) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM BOOK_DETAIL where BOOK_ID = ?");

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
    public cat.uvic.teknos.library.models.BookDetail get(Integer id) {
        String query = "SELECT * FROM BOOK_DETAIL WHERE BOOK_ID = ?";
        String query2 = "SELECT * FROM BOOK WHERE BOOK_ID = ?";


        try (PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement bookStatement = connection.prepareStatement(query2);
        ) {
            statement.setInt(1, id);
            bookStatement.setInt(1, id);


            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cat.uvic.teknos.library.models.BookDetail result = new BookDetail();
                    result.setId(resultSet.getInt("BOOK_ID"));
                    result.setDescription(resultSet.getString("DESCRIPTION"));
                    result.setReviews(resultSet.getString("REVIEWS"));

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
    public Set<cat.uvic.teknos.library.models.BookDetail> getAll() {
        String query = "SELECT * FROM BOOK_DETAIL";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (var resultSet = statement.executeQuery()) {
                var bookDetails = new HashSet<cat.uvic.teknos.library.models.BookDetail>();

                while (resultSet.next()) {
                    cat.uvic.teknos.library.models.BookDetail result = new BookDetail();
                    result.setId(resultSet.getInt("BOOK_ID"));
                    result.setDescription(resultSet.getString("DESCRIPTION"));
                    result.setReviews(resultSet.getString("REVIEWS"));

                    bookDetails.add(result);
                }
                return bookDetails;
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