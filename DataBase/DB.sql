DROP DATABASE IF EXISTS LIBRARY;
CREATE DATABASE LIBRARY;
USE LIBRARY;
CREATE TABLE AUTHOR (
                        AUTHOR_ID INT PRIMARY KEY AUTO_INCREMENT,
                        FIRST_NAME VARCHAR(50),
                        LAST_NAME VARCHAR(50)
);
CREATE TABLE GENRE (
                       GENRE_ID INT PRIMARY KEY AUTO_INCREMENT,
                       NAME VARCHAR(50)
);
CREATE TABLE CUSTOMER (
                          CUSTOMER_ID INT PRIMARY KEY AUTO_INCREMENT,
                          FIRST_NAME VARCHAR(50),
                          LAST_NAME VARCHAR(50),
                          EMAIL VARCHAR(100),
                          ADDRESS VARCHAR(255),
                          PHONE_NUMBER VARCHAR(20)
);
CREATE TABLE BOOK (
                      BOOK_ID INT PRIMARY KEY AUTO_INCREMENT,
                      TITLE VARCHAR(100),
                      AUTHOR_ID INT,
                      PUBLICATION_DATE DATE,
                      ISBN VARCHAR(20),
                      COPIES INT,
                      FOREIGN KEY (AUTHOR_ID) REFERENCES AUTHOR(AUTHOR_ID) ON DELETE CASCADE
);
CREATE TABLE LOAN (
                      BOOK_ID INT PRIMARY KEY,
                      CUSTOMER_ID INT,
                      LOAN_DATE DATE,
                      RETURN_DATE DATE,
                      FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(CUSTOMER_ID) ON DELETE CASCADE ,
                      FOREIGN KEY (BOOK_ID) REFERENCES BOOK(BOOK_ID) ON DELETE CASCADE
);
CREATE TABLE BOOK_DETAIL (
                             BOOK_ID INT PRIMARY KEY,
                             DESCRIPTION TEXT,
                             REVIEWS TEXT,
                             FOREIGN KEY (BOOK_ID) REFERENCES BOOK(BOOK_ID) ON DELETE CASCADE
);
CREATE TABLE BOOK_GENRE (
                            BOOK_ID INT,
                            GENRE_ID INT,
                            PRIMARY KEY (BOOK_ID, GENRE_ID),
                            FOREIGN KEY (BOOK_ID) REFERENCES BOOK(BOOK_ID) ON DELETE CASCADE ,
                            FOREIGN KEY (GENRE_ID) REFERENCES GENRE(GENRE_ID) ON DELETE CASCADE
);