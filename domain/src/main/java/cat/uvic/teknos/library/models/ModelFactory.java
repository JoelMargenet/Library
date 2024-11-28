package cat.uvic.teknos.library.models;

public interface ModelFactory {
    Author createAuthor();
    Book createBook();
    BookDetail createBookDetail();
    Customer createCustomer();
    Genre createGenre();
    Loan createLoan();
}
