package cat.uvic.teknos.library.models;

import java.sql.Date;
import java.util.Set;


public interface Book {
    int getId();
    void setId(int id);

    String getTitle();
    void setTitle(String title);

    Date getPublicationDate();
    void setPublicationDate(Date publicationDate);

    String getISBN();
    void setISBN(String isbn);

    int getCopies();
    void setCopies(int copies);

    Author getAuthor();
    void setAuthor(Author author);

    Set<Genre> getGenres();
    void setGenres(Set<Genre> genre);

    BookDetail getDetail();
    void setDetail(BookDetail bookDetail);

    Loan getLoan();
    void setLoan(Loan loan);

}
