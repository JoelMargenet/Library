package cat.uvic.teknos.library.domain.jpa.resources.models;

import cat.uvic.teknos.library.models.Book;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "AUTHOR")
public class Author implements cat.uvic.teknos.library.models.Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHOR_ID")
    private int id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @OneToMany(mappedBy = "author",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            targetEntity = cat.uvic.teknos.library.domain.jpa.resources.models.Book.class)
    private Set<Book> books = new HashSet<>();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Set<Book> getBooks() {
        return books;
    }

    @Override
    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}