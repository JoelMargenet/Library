package cat.uvic.teknos.library.backoffice;

import cat.uvic.teknos.library.models.ModelFactory;
import cat.uvic.teknos.library.models.Customer;
import cat.uvic.teknos.library.repositories.CustomerRepository;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Set;

import static cat.uvic.teknos.library.backoffice.IOUtils.readLine;

public class CustomersManager {
    private final BufferedReader in;
    private final PrintStream out;
    private final CustomerRepository customerRepository;
    private final ModelFactory modelFactory;

    public CustomersManager(BufferedReader in, PrintStream out, CustomerRepository customerRepository, ModelFactory modelFactory) {
        this.in = in;
        this.out = out;
        this.customerRepository = customerRepository;
        this.modelFactory = modelFactory;
    }

    public void start() {
        out.println("Customers:");
        out.println("1 to insert a new Customer");
        out.println("2 to update a Customer");
        out.println("3 to show all Customers");
        out.println("4 to get Customer by ID");
        out.println("5 to delete Customer by ID");
        out.println("Write exit to go to the main menu \n");

        var command = "";
        do {
            command = readLine(in);

            try {
                switch (command) {
                    case "1" -> insert();
                    case "2" -> update();
                    case "3" -> getAll();
                    case "4" -> getById();
                    case "5" -> deleteById();
                }
            } catch (Exception e) {
                out.println("An error occurred: " + e.getMessage());
                e.printStackTrace(out);
            }

        } while (!command.equals("exit"));

        out.println("Bye!");
    }

    private void insert() {
        var customer = modelFactory.createCustomer();

        out.println("First name:");
        customer.setFirstName(readLine(in));

        out.println("Last name:");
        customer.setLastName(readLine(in));

        out.println("Email:");
        customer.setEmail(readLine(in));

        out.println("Address:");
        customer.setAddress(readLine(in));

        out.println("Phone number:");
        customer.setPhoneNumber(readLine(in));

        try {
            customerRepository.save(customer);
            out.println("Inserted customer successfully: " + customer);
        } catch (Exception e) {
            out.println("Failed to insert customer: " + e.getMessage());
            e.printStackTrace(out);
        }

        out.println();
        start();
    }

    private void update() {
        out.println("Update Customer:");
        out.println("Enter the ID of the customer you want to update:");
        int customerId = Integer.parseInt(readLine(in));

        var customer = customerRepository.get(customerId);
        if (customer == null) {
            out.println("Customer not found with ID: " + customerId);
            return;
        }

        out.println("Enter the new first name:");
        customer.setFirstName(readLine(in));

        out.println("Enter the new last name:");
        customer.setLastName(readLine(in));

        out.println("Enter the new email:");
        customer.setEmail(readLine(in));

        out.println("Enter the new address:");
        customer.setAddress(readLine(in));

        out.println("Enter the new phone number:");
        customer.setPhoneNumber(readLine(in));

        try {
            customerRepository.save(customer);
            out.println("Updated customer successfully: " + customer);
        } catch (Exception e) {
            out.println("Failed to update customer: " + e.getMessage());
            e.printStackTrace(out);
        }

        out.println();
        start();
    }

    private void getAll() {
        Set<Customer> customers = customerRepository.getAll();
        if (customers.isEmpty()) {
            out.println("No customers found.");
            return;
        }

        var asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("ID", "First Name", "Last Name", "Email", "Address", "Phone Number");
        asciiTable.addRule();

        for (Customer customer : customers) {
            asciiTable.addRow(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getAddress(), customer.getPhoneNumber());
            asciiTable.addRule();
        }

        asciiTable.setTextAlignment(TextAlignment.CENTER);
        String render = asciiTable.render();
        out.println(render);

        out.println();
        start();
    }

    private void getById() {
        out.println("Enter the ID of the customer to retrieve:");
        int id = Integer.parseInt(readLine(in));
        Customer customer = customerRepository.get(id);
        if (customer != null) {
            out.println("Customer found: " + customer.getId());
            out.println("First name: " + customer.getFirstName());
            out.println("Last name: " + customer.getLastName());
            out.println("Email: " + customer.getEmail());
            out.println("Address: " + customer.getAddress());
            out.println("Phone number: " + customer.getPhoneNumber());
        } else {
            out.println("Customer not found with ID: " + id);
        }

        out.println();
        start();
    }

    private void deleteById() {
        out.println("Enter the ID of the customer to delete:");
        int id = Integer.parseInt(readLine(in));
        Customer customer = customerRepository.get(id);
        if (customer != null) {
            try {
                customerRepository.delete(customer);
                out.println("Customer deleted successfully with ID: " + id);
            } catch (Exception e) {
                out.println("Failed to delete customer: " + e.getMessage());
                e.printStackTrace(out);
            }
        } else {
            out.println("Customer not found with ID: " + id);
        }

        out.println();
        start();
    }
}