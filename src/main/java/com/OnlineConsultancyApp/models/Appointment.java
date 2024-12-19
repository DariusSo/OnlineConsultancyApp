package com.OnlineConsultancyApp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

    private long id;
    private String title;
    private String description;
    private Enum category;
    private long userId;
    private long consultantId;
    private LocalDateTime timeAndDate;
    private BigDecimal price;
    private boolean isAccepted;

}
