package cat.library.services.controllers;

import cat.library.services.utils.Mappers;
import cat.uvic.teknos.library.domain.jdbc.models.Customer;
import cat.uvic.teknos.library.repositories.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomerController implements Controller {
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper = Mappers.get();

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public String get() {
        try {
            var customers = customerRepository.getAll();
            return objectMapper.writeValueAsString(customers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // GET a single Customer by ID
    public String get(int id) {
        var customer = customerRepository.get(id);
        if (customer == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }


    public String post(String customerJson) {
        try {
            Customer customer = objectMapper.readValue(customerJson, Customer.class);
            customerRepository.save(customer);
            return "{\"message\": \"Customer created successfully\", \"id\": " + customer.getId() + "}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while creating customer\"}";
        }
    }

    public String put(int id, String customerJson) {
        try {
            var updatedCustomer = objectMapper.readValue(customerJson, Customer.class);
            updatedCustomer.setId(id);

            var existingCustomer = customerRepository.get(id);
            if (existingCustomer != null) {
                existingCustomer.setFirstName(updatedCustomer.getFirstName());
                existingCustomer.setLastName(updatedCustomer.getLastName());
                existingCustomer.setEmail(updatedCustomer.getEmail());
                existingCustomer.setAddress(updatedCustomer.getAddress());
                existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());

                customerRepository.save(existingCustomer);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ("{ \"error\": \"Invalid JSON format.\" }");
        } catch (Exception e) {
            e.printStackTrace();
            return ("{ \"error\": \"Error updating customer.\" }");
        }
        return "{}";
    }

    public String delete(int id) {
        var customer = customerRepository.get(id);
        if (customer == null) {
            return "{ \"error\": \"Customer not found.\" }";
        }
        try {
            customerRepository.delete(customer);
            return "{ \"message\": \"Customer deleted successfully.\" }";
        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"Error deleting customer.\" }";
        }
    }
}