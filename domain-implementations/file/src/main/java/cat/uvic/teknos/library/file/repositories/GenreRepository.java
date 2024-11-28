package cat.uvic.teknos.library.file.repositories;

import cat.uvic.teknos.library.models.Genre;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GenreRepository implements cat.uvic.teknos.library.repositories.GenreRepository {
    private static Map<Integer, Genre > genres = new HashMap<>();
    
    public static void load() {
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try(var inputStream = new ObjectInputStream(new FileInputStream(currentDirectory + "genres.ser"))) {
            genres = (Map<Integer, Genre>) inputStream.readObject();
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

        try(var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "genres.ser"))) {
            outputStream.writeObject(genres);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void save(Genre model) {
        if (model.getId() <= 0) {
            var newId = genres.keySet().stream().mapToInt(k -> k).max().orElse(0) + 1;
            //model.setId(newId);

            genres.put(newId, model);
        } else {
            genres.put(model.getId(), model);
        }
        write();
    }

    public void update(){
        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "genres.ser"))) {
            outputStream.writeObject(genres);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void delete(Genre model) {

        var currentDirectory = System.getProperty("user.dir") + "/src/main/resources/";

        try (var outputStream = new ObjectOutputStream(new FileOutputStream(currentDirectory + "genres.ser"))) {

            for (Iterator<Map.Entry<Integer, Genre>> iterator = genres.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Genre> entry = iterator.next();
                if (entry.getValue().equals(model)) {
                    iterator.remove();
                    break;
                }
            }
            outputStream.writeObject(genres);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Genre get(Integer id) {
        return genres.get(id);
    }

    @Override
    public Set<Genre> getAll() {
        return Set.copyOf(genres.values());
    }
}