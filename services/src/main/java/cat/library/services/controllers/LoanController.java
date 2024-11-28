package cat.library.services.controllers;

import cat.library.services.utils.Mappers;
import cat.uvic.teknos.library.domain.jdbc.models.Loan;
import cat.uvic.teknos.library.repositories.LoanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoanController implements Controller {
    private final LoanRepository loanRepository;
    private final ObjectMapper objectMapper = Mappers.get();

    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public String get() {
        try {
            var loans = loanRepository.getAll();
            return objectMapper.writeValueAsString(loans);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // GET a single Loan by Book ID
    public String get(int bookId) {
        var loan = loanRepository.get(bookId);
        if (loan == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(loan);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // POST to create a new Loan
    public String post(String loanJson) {
        try {
            System.out.println("Received POST request with payload: " + loanJson);
            Loan loan = objectMapper.readValue(loanJson, Loan.class);
            loanRepository.save(loan);
            return "{\"message\": \"Loan created successfully\", \"bookId\": " + loan.getBook().getId() + "}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while creating loan: " + e.getMessage() + "\"}";
        }
    }

    // PUT to update an existing Loan
    public String put(int bookId, String loanJson) {
        try {
            System.out.println("Received PUT request for bookId " + bookId + " with payload: " + loanJson);
            Loan updatedLoan = objectMapper.readValue(loanJson, Loan.class);

            var existingLoan = loanRepository.get(bookId);
            if (existingLoan != null) {
                existingLoan.setCustomer(updatedLoan.getCustomer());
                existingLoan.setLoanDate(updatedLoan.getLoanDate());
                existingLoan.setReturnDate(updatedLoan.getReturnDate());

                loanRepository.save(existingLoan);
                return "{\"message\": \"Loan updated successfully\"}";
            } else {
                return "{\"error\": \"Loan not found\"}";
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error updating loan: " + e.getMessage() + "\"}";
        }
    }

    public String delete(int bookId) {
        var loan = loanRepository.get(bookId);
        if (loan == null) {
            return "{ \"error\": \"Loan not found.\" }";
        }
        try {
            loanRepository.delete(loan);
            return "{ \"message\": \"Loan deleted successfully.\" }";
        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"Error deleting loan.\" }";
        }
    }
}