package cat.uvic.teknos.library.models;

import java.util.Set;

public interface Customer {
    int getId();
    void setId(int id);

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    String getEmail();
    void setEmail(String email);

    String getAddress();
    void setAddress(String address);

    String getPhoneNumber();
    void setPhoneNumber(String phoneNumber);

    Set<Loan> getLoans();
    void setLoans(Set<Loan> loans);
}