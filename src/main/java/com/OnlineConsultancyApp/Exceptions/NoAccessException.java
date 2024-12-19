package com.OnlineConsultancyApp.Exceptions;

public class NoAccessException extends RuntimeException{
    public NoAccessException() {
    }

    public NoAccessException(String message) {
        super(message);
    }

    public NoAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
