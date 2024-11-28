package cat.uvic.teknos.library.domain.jpa.resources.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "GENRE")
public class Genre implements cat.uvic.teknos.library.models.Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GENRE_ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @ManyToMany(mappedBy = "genres", targetEntity = Book.class)
    private Set<cat.uvic.teknos.library.models.Book> books = new HashSet<>();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<cat.uvic.teknos.library.models.Book> getBooks() {
        return books;
    }

    @Override
    public void setBooks(Set<cat.uvic.teknos.library.models.Book> books) {
        this.books = books;
    }
}