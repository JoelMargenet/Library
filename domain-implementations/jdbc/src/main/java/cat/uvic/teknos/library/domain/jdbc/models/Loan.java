package cat.uvic.teknos.library.domain.jdbc.models;

import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.Customer;

import java.util.Date;

public class Loan implements cat.uvic.teknos.library.models.Loan {
    private int id;
    private Date loanDate;
    private Date returnDate;
    private Customer customer;
    private Book book;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public java.sql.Date getLoanDate() {
        return (java.sql.Date) loanDate;
    }

    @Override
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    @Override
    public java.sql.Date getReturnDate() {
        return (java.sql.Date) returnDate;
    }

    @Override
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Book getBook() {
        return book;
    }

    @Override
    public void setBook(Book book) {
        this.book = book;
    }
}