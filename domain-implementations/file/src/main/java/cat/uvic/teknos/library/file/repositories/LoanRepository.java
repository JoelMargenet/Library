package cat.uvic.teknos.library.file.repositories;

import cat.uvic.teknos.library.models.Loan;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LoanRepository implements cat.uvic.teknos.library.repositories.LoanRepository {
    private static Map<Integer, Loan > loans = new HashMap<>();
    
    public static void load() {
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try(var inputStream = new ObjectInputStream(new FileInputStream(currentDirectory + "loans.ser"))) {
            loans = (Map<Integer, Loan>) inputStream.readObject();
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

        try(var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "loans.ser"))) {
            outputStream.writeObject(loans);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void save(Loan model) {
        if (model.getId() <= 0) {
            var newId = loans.keySet().stream().mapToInt(k -> k).max().orElse(0) + 1;
            //model.setId(newId);

            loans.put(newId, model);
        } else {
            loans.put(model.getId(), model);
        }
        write();
    }

    public void update(){
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "loans.ser"))) {
            outputStream.writeObject(loans);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void delete(Loan model) {

        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "loans.ser"))) {

            for (Iterator<Map.Entry<Integer, Loan>> iterator = loans.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Loan> entry = iterator.next();
                if (entry.getValue().equals(model)) {
                    iterator.remove();
                    break;
                }
            }
            outputStream.writeObject(loans);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Loan get(Integer id) {
        return loans.get(id);
    }

    @Override
    public Set<Loan> getAll() {
        return Set.copyOf(loans.values());
    }
}