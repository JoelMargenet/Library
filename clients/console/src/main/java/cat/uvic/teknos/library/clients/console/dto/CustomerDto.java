package cat.uvic.teknos.library.clients.console.dto;

import cat.uvic.teknos.library.models.Customer;
import cat.uvic.teknos.library.models.Loan;

import java.util.HashSet;
import java.util.Set;

public class CustomerDto implements Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private Set<Loan> loans = new HashSet<>();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Set<Loan> getLoans() {
        return loans;
    }

    @Override
    public void setLoans(Set<Loan> loans) {
        this.loans = loans;
    }
}
