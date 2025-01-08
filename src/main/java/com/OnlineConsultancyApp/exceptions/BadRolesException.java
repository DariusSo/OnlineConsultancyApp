package com.OnlineConsultancyApp.exceptions;

public class BadRolesException extends RuntimeException{
    public BadRolesException() {
    }

    public BadRolesException(String message) {
        super(message);
    }

    public BadRolesException(String message, Throwable cause) {
        super(message, cause);
    }
}
