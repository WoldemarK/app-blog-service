package ru.yandex.exception;

public class CommentNotFoundException extends  RuntimeException {

    private final Long id;
    public CommentNotFoundException(String message, Throwable cause, Long id) {
        super(message, cause);
        this.id = id;
    }

}
