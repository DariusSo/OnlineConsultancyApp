package com.OnlineConsultancyApp.exceptions;

public class ThereIsNoSuchRoleException extends RuntimeException{
    public ThereIsNoSuchRoleException() {
    }

    public ThereIsNoSuchRoleException(String message) {
        super(message);
    }

    public ThereIsNoSuchRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
