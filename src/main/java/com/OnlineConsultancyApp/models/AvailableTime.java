package com.OnlineConsultancyApp.models;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class AvailableTime {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm") // Match your date format
    private LocalDateTime date;

    // Getters and Setters
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
