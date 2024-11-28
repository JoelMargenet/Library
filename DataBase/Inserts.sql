USE LIBRARY;

-- Insert authors
INSERT INTO AUTHOR (FIRST_NAME, LAST_NAME)
VALUES ('George', 'Orwell'),
       ('J.R.R.', 'Tolkien'),
       ('F. Scott', 'Fitzgerald');

-- Insert genres
INSERT INTO GENRE (NAME)
VALUES ('Dystopian Fiction'),
       ('Fantasy'),
       ('Classic');

-- Insert customers
INSERT INTO CUSTOMER (FIRST_NAME, LAST_NAME, EMAIL, ADDRESS, PHONE_NUMBER)
VALUES ('John', 'Doe', 'john.doe@example.com', '123 Main St, Anytown, USA', '555-1234'),
       ('Jane', 'Smith', 'jane.smith@example.com', '456 Oak St, Anytown, USA', '555-5678');

-- Insert books
INSERT INTO BOOK (TITLE, AUTHOR_ID, PUBLICATION_DATE, ISBN, COPIES)
VALUES ('1984', 1, '1949-06-08', '9780451524935', 10),
       ('Animal Farm', 1, '1945-08-17', '9780451526342', 8),
       ('The Hobbit', 2, '1937-09-21', '9780261102217', 15),
       ('The Great Gatsby', 3, '1925-04-10', '978-3-16-148410-0', 20);

-- Insert loans
INSERT INTO LOAN (CUSTOMER_ID, BOOK_ID, LOAN_DATE, RETURN_DATE)
VALUES (1, 1, '2024-02-29', '2024-03-14'),
       (2, 3, '2024-02-28', '2024-03-14');

-- Insert book detail
INSERT INTO BOOK_DETAIL (BOOK_ID, DESCRIPTION, REVIEWS)
VALUES (1, 'A dystopian social science fiction novel.', 'Highly acclaimed for its portrayal of government surveillance and totalitarianism.'),
       (2, 'An allegorical novella about a group of farm animals who rebel against their human farmer.', 'Considered a classic and a satirical commentary on totalitarianism.'),
       (3, 'A fantasy novel about the quest of hobbit Bilbo Baggins to win a share of the treasure guarded by Smaug the dragon.', 'A beloved classic of children''s literature and the precursor to The Lord of the Rings trilogy.');
