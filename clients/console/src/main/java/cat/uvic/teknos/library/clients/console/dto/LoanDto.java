package cat.uvic.teknos.library.clients.console.dto;

import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.Customer;
import cat.uvic.teknos.library.models.Loan;

import java.sql.Date;

public class LoanDto implements Loan {
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
    public Date getLoanDate() {
        return loanDate;
    }

    @Override
    public void setLoanDate(java.util.Date loanDate) {
        this.loanDate = (Date) loanDate;
    }


    @Override
    public Date getReturnDate() {
        return returnDate;
    }

    @Override
    public void setReturnDate(java.util.Date returnDate) {
        this.returnDate = (Date) returnDate;
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
