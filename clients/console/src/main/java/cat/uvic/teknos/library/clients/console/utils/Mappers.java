package cat.uvic.teknos.library.clients.console.utils;

import cat.uvic.teknos.library.clients.console.dto.*;
import cat.uvic.teknos.library.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Mappers {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper
                .registerModule(new JavaTimeModule()) // Registered to map Java 8 Date and Time API (LocalDate, LocalDateTime, etc.)
                .registerModule(
                        new SimpleModule()
                                .addAbstractTypeMapping(Genre.class, GenreDto.class) // Mapping Genre to GenreDto
                )
                .registerModule(
                        new SimpleModule()
                                .addAbstractTypeMapping(Book.class, BookDto.class) // Mapping Book to BookDto
                )
                .registerModule(
                        new SimpleModule()
                                .addAbstractTypeMapping(BookDetail.class, BookDetailDto.class) // Mapping BookDetail to BookDetailDto
                )
                .registerModule(
                        new SimpleModule()
                                .addAbstractTypeMapping(Loan.class, LoanDto.class) // Mapping Loan to LoanDto
                )
                .registerModule(
                        new SimpleModule()
                                .addAbstractTypeMapping(Author.class, AuthorDto.class) // Mapping Author to AuthorDto
                )
                .registerModule(
                        new SimpleModule()
                                .addAbstractTypeMapping(Customer.class, CustomerDto.class) // Mapping Customer to CustomerDto
                );
    }

    public static ObjectMapper get() {
        return mapper;
    }
}
