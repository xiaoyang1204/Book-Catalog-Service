package com.example.bookcatalog.domain.exception;

public class DuplicateIsbnException extends RuntimeException {

    public DuplicateIsbnException(String isbn) {
        super("Book with ISBN '" + isbn + "' already exists");
    }
}
