package com.OnlineConsultancyApp.models;

import com.OnlineConsultancyApp.enums.Categories;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

    private long id;
    private String uuid;
    private String title;
    private String description;
    private Categories category;
    private long userId;
    private long consultantId;
    private LocalDateTime timeAndDate;
    private BigDecimal price;
    private boolean isAccepted;
    private boolean isPaid;
    private UUID roomUuid;

}
