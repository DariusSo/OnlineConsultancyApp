package com.OnlineConsultancyApp.exceptions;

public class BadEmailOrPasswordException extends RuntimeException{
    public BadEmailOrPasswordException() {
    }

    public BadEmailOrPasswordException(String message) {
        super(message);
    }

    public BadEmailOrPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
