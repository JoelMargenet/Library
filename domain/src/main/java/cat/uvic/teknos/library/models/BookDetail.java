package cat.uvic.teknos.library.models;

public interface BookDetail {
    int getId();

    void setId(int id);

    String getDescription();

    void setDescription(String description);

    String getReviews();

    void setReviews(String reviews);

    Book getBook();
    void setBook(Book book);

}