package ru.yandex.exception;

public class InvalidFileTypeException extends  RuntimeException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
