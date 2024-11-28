package cat.uvic.teknos.library.file.repositories;

import cat.uvic.teknos.library.models.Customer;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CustomerRepository implements cat.uvic.teknos.library.repositories.CustomerRepository {
    private static Map<Integer, Customer > customers = new HashMap<>();
    
    public static void load() {
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try(var inputStream = new ObjectInputStream(new FileInputStream(currentDirectory + "customers.ser"))) {
            customers = (Map<Integer, Customer>) inputStream.readObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void write() {
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try(var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "customers.ser"))) {
            outputStream.writeObject(customers);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void save(Customer model) {
        if (model.getId() <= 0) {
            var newId = customers.keySet().stream().mapToInt(k -> k).max().orElse(0) + 1;
            //model.setId(newId);

            customers.put(newId, model);
        } else {
            customers.put(model.getId(), model);
        }
        write();
    }

    public void update(){
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "customers.ser"))) {
            outputStream.writeObject(customers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void delete(Customer model) {

        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "customers.ser"))) {

            for (Iterator<Map.Entry<Integer, Customer>> iterator = customers.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Customer> entry = iterator.next();
                if (entry.getValue().equals(model)) {
                    iterator.remove();
                    break;
                }
            }
            outputStream.writeObject(customers);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer get(Integer id) {
        return customers.get(id);
    }

    @Override
    public Set<Customer> getAll() {
        return Set.copyOf(customers.values());
    }
}