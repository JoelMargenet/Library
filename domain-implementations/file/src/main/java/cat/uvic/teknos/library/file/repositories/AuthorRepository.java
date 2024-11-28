package cat.uvic.teknos.library.file.repositories;

import cat.uvic.teknos.library.models.Author;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AuthorRepository implements cat.uvic.teknos.library.repositories.AuthorRepository {
    private static Map<Integer, Author> authors = new HashMap<>();

    public static void load() {
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try(var inputStream = new ObjectInputStream(new FileInputStream(currentDirectory + "authors.ser"))) {
            authors = (Map<Integer, Author>) inputStream.readObject();
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

        try(var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "authors.ser"))) {
            outputStream.writeObject(authors);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Author model) {
        if (model.getId() <= 0) {
            var newId = authors.keySet().stream().mapToInt(k -> k).max().orElse(0) + 1;
            //model.setId(newId);

            authors.put(newId, model);
        } else {
            authors.put(model.getId(), model);
        }
        write();
    }

    public void update(){
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "authors.ser"))) {
            outputStream.writeObject(authors);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Author model) {

        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "authors.ser"))) {

            for (Iterator<Map.Entry<Integer, Author>> iterator = authors.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Author> entry = iterator.next();
                if (entry.getValue().equals(model)) {
                    iterator.remove();
                    break;
                }
            }
            outputStream.writeObject(authors);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Author get(Integer id) {
        return authors.get(id);
    }

    @Override
    public Set<Author> getAll() {
        return Set.copyOf(authors.values());
    }
}