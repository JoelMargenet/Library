package cat.library.services.utils;


import cat.uvic.teknos.library.domain.jdbc.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Mappers {
    private static final ObjectMapper mapper;

    static  {
        final var genreTypeMapping = new SimpleModule()
                .addAbstractTypeMapping(cat.uvic.teknos.library.models.Author.class, Author.class)
                .addAbstractTypeMapping(cat.uvic.teknos.library.models.Book.class, Book.class)
                .addAbstractTypeMapping(cat.uvic.teknos.library.models.BookDetail.class, BookDetail.class)
                .addAbstractTypeMapping(cat.uvic.teknos.library.models.Genre.class, Genre.class)
                .addAbstractTypeMapping(cat.uvic.teknos.library.models.Customer.class, Customer.class)
                .addAbstractTypeMapping(cat.uvic.teknos.library.models.Loan.class, Loan.class);
        mapper = new ObjectMapper();
        mapper
                .registerModule(new JavaTimeModule()) // Registered to map LocalDate (add implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.0" to build.gradle.kts) )
                .registerModule(genreTypeMapping); // Registered to map the Genre deserialization
    }

    public static ObjectMapper get() {
        return mapper;
    }
}