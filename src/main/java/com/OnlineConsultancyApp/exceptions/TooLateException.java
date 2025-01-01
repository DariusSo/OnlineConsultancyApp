package com.OnlineConsultancyApp.exceptions;

public class TooLateException extends RuntimeException{
    public TooLateException() {
    }

    public TooLateException(String message) {
        super(message);
    }

    public TooLateException(String message, Throwable cause) {
        super(message, cause);
    }
}
