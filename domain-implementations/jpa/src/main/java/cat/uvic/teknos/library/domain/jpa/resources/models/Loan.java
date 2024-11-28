package cat.uvic.teknos.library.domain.jpa.resources.models;

import cat.uvic.teknos.library.models.Book;
import cat.uvic.teknos.library.models.Customer;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "LOAN")
public class Loan implements cat.uvic.teknos.library.models.Loan {

    @Id
    @Column(name = "BOOK_ID")
    private int bookId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "BOOK_ID")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    @Column(name = "LOAN_DATE")
    private Date loanDate;

    @Column(name = "RETURN_DATE")
    private Date returnDate;

    @Override
    public Book getBook() {
        return book;
    }

    @Override
    public void setBook(Book book) {
        this.book = book;
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
    public int getId() {
        return bookId;
    }

    @Override
    public void setId(int id) {
        this.bookId = id;
    }

    @Override
    public Date getLoanDate() {
        return loanDate;
    }

    @Override
    public void setLoanDate(java.util.Date loanDate) {
        this.loanDate = new Date(loanDate.getTime());
    }

    @Override
    public Date getReturnDate() {
        return returnDate;
    }

    @Override
    public void setReturnDate(java.util.Date returnDate) {
        this.returnDate = new Date(returnDate.getTime());
    }
}