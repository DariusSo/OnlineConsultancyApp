package com.OnlineConsultancyApp.Exceptions;

public class NoSuchAppointmentException extends RuntimeException{

    public NoSuchAppointmentException() {
    }

    public NoSuchAppointmentException(String message) {
        super(message);
    }

    public NoSuchAppointmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
