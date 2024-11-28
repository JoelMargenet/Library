package cat.uvic.teknos.library.file.repositories;

import cat.uvic.teknos.library.models.Book;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BookRepository implements cat.uvic.teknos.library.repositories.BookRepository {
    private static Map<Integer, Book > books = new HashMap<>();
    
    public static void load() {
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try(var inputStream = new ObjectInputStream(new FileInputStream(currentDirectory + "books.ser"))) {
            books = (Map<Integer, Book>) inputStream.readObject();
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

        try(var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "books.ser"))) {
            outputStream.writeObject(books);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void save(Book model) {
        if (model.getId() <= 0) {
            var newId = books.keySet().stream().mapToInt(k -> k).max().orElse(0) + 1;
            //model.setId(newId);

            books.put(newId, model);
        } else {
            books.put(model.getId(), model);
        }
        write();
    }

    public void update(){
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "books.ser"))) {
            outputStream.writeObject(books);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void delete(Book model) {

        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "books.ser"))) {

            for (Iterator<Map.Entry<Integer, Book>> iterator = books.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Book> entry = iterator.next();
                if (entry.getValue().equals(model)) {
                    iterator.remove();
                    break;
                }
            }
            outputStream.writeObject(books);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Book get(Integer id) {
        return books.get(id);
    }

    @Override
    public Set<Book> getAll() {
        return Set.copyOf(books.values());
    }
}