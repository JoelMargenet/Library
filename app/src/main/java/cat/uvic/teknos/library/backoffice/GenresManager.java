package cat.uvic.teknos.library.backoffice;

import cat.uvic.teknos.library.models.ModelFactory;
import cat.uvic.teknos.library.models.Genre;
import cat.uvic.teknos.library.repositories.GenreRepository;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Set;

import static cat.uvic.teknos.library.backoffice.IOUtils.readLine;

public class GenresManager {
    private final BufferedReader in;
    private final PrintStream out;
    private final GenreRepository genreRepository;
    private final ModelFactory modelFactory;

    public GenresManager(BufferedReader in, PrintStream out, GenreRepository genreRepository, ModelFactory modelFactory) {
        this.in = in;
        this.out = out;
        this.genreRepository = genreRepository;
        this.modelFactory = modelFactory;
    }

    public void start() {
        out.println("Genres:");
        out.println("1 to insert a new Genre");
        out.println("2 to update a Genre");
        out.println("3 to show all Genres");
        out.println("4 to get Genre by ID");
        out.println("5 to delete Genre by ID");
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
        var genre = modelFactory.createGenre();

        out.println("Name:");
        genre.setName(readLine(in));

        try {
            genreRepository.save(genre);
            out.println("Inserted genre successfully: " + genre);
        } catch (Exception e) {
            out.println("Failed to insert genre");
            try {
                genreRepository.save(genre);
                out.println("Inserted genre successfully: " + genre);
            } catch (Exception a) {
                out.println("Failed to insert genre: " + a.getMessage());
                a.printStackTrace(out);
            }
        }

        out.println();
        start();
    }

        private void update() {
            out.println("Update Genre:");
            out.println("Enter the ID of the genre you want to update:");
            int genreId = Integer.parseInt(readLine(in));

            var genre = genreRepository.get(genreId);
            if (genre == null) {
                out.println("Genre not found with ID: " + genreId);
                return;
            }

            out.println("Enter the new name:");
            genre.setName(readLine(in));

            try {
                genreRepository.save(genre);
                out.println("Updated genre successfully: " + genre);
            } catch (Exception e) {
                out.println("Failed to update genre: " + e.getMessage());
                e.printStackTrace(out);
            }

            out.println();
            start();

        }

        private void getAll() {
            Set<Genre> genres = genreRepository.getAll();
            if (genres.isEmpty()) {
                out.println("No genres found.");
                return;
            }

            var asciiTable = new AsciiTable();
            asciiTable.addRule();
            asciiTable.addRow("ID", "Name");
            asciiTable.addRule();

            for (Genre genre : genres) {
                asciiTable.addRow(genre.getId(), genre.getName());
                asciiTable.addRule();
            }

            asciiTable.setTextAlignment(TextAlignment.CENTER);
            String render = asciiTable.render();
            out.println(render);

            out.println();
            start();
        }

        private void getById() {
            out.println("Enter the ID of the genre to retrieve:");
            int id = Integer.parseInt(readLine(in));
            Genre genre = genreRepository.get(id);
            if (genre != null) {
                out.println("Genre found: " + genre.getId());
                out.println("Name: " + genre.getName());
            } else {
                out.println("Genre not found with ID: " + id);
            }

            out.println();
            start();
        }

        private void deleteById() {
            out.println("Enter the ID of the genre to delete:");
            int id = Integer.parseInt(readLine(in));
            Genre genre = genreRepository.get(id);
            if (genre != null) {
                try {
                    genreRepository.delete(genre);
                    out.println("Genre deleted successfully with ID: " + id);
                } catch (Exception e) {
                    out.println("Failed to delete genre: " + e.getMessage());
                    e.printStackTrace(out);
                }
            } else {
                out.println("Genre not found with ID: " + id);
            }

            out.println();
            start();
        }
    }