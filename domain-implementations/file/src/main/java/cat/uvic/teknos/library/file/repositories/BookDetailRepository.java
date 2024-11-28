package cat.uvic.teknos.library.file.repositories;

import cat.uvic.teknos.library.models.BookDetail;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BookDetailRepository implements cat.uvic.teknos.library.repositories.BookDetailRepository {
    private static Map<Integer, BookDetail> bookdetail = new HashMap<>();
    
    public static void load() {
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try(var inputStream = new ObjectInputStream(new FileInputStream(currentDirectory + "bookdetail.ser"))) {
            bookdetail = (Map<Integer, BookDetail>) inputStream.readObject();
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

        try(var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "bookdetail.ser"))) {
            outputStream.writeObject(bookdetail);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void save(BookDetail model) {
        if (model.getId() <= 0) {
            var newId = bookdetail.keySet().stream().mapToInt(k -> k).max().orElse(0) + 1;
            //model.setId(newId);

            bookdetail.put(newId, model);
        } else {
            bookdetail.put(model.getId(), model);
        }
        write();
    }

    public void update(){
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "bookdetail.ser"))) {
            outputStream.writeObject(bookdetail);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void delete(BookDetail model) {

        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "bookdetail.ser"))) {

            for (Iterator<Map.Entry<Integer, BookDetail>> iterator = bookdetail.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, BookDetail> entry = iterator.next();
                if (entry.getValue().equals(model)) {
                    iterator.remove();
                    break;
                }
            }
            outputStream.writeObject(bookdetail);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BookDetail get(Integer id) {
        return bookdetail.get(id);
    }

    @Override
    public Set<BookDetail> getAll() {
        return Set.copyOf(bookdetail.values());
    }
}