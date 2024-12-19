package com.OnlineConsultancyApp.models;

import com.OnlineConsultancyApp.enums.Categories;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Consultant extends User{

    private String categories;
    private String availableTime;
    private String speciality;
    private String description;
    private BigDecimal hourlyRate;

    public Consultant(long id, String firstName, String lastName, String email, String phone, String appointmentsId, Enum role, String categories, String availableTime, String speciality, String description, BigDecimal hourlyRate) {
        super(id, firstName, lastName, email, phone, appointmentsId, role);
        this.categories = categories;
        this.availableTime = availableTime;
        this.speciality = speciality;
        this.description = description;
        this.hourlyRate = hourlyRate;
    }
}
