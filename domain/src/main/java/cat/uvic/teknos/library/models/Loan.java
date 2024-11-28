package cat.uvic.teknos.library.models;

import java.util.Date;

public interface Loan {
    int getId();
    void setId(int id);

    java.sql.Date getLoanDate();
    void setLoanDate(Date loanDate);

    java.sql.Date getReturnDate();
    void setReturnDate(Date returnDate);

    Customer getCustomer();
    void setCustomer(Customer customer);

    Book getBook();
    void setBook(Book book);
}