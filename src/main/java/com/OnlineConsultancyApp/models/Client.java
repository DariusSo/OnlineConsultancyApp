package com.OnlineConsultancyApp.models;

import com.OnlineConsultancyApp.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client extends User{

    private LocalDate birthDate;

    public Client(long id, String firstName, String lastName, String email, String phone, String appointmentsId, Roles role, LocalDate birthDate) {
        super(id, firstName, lastName, email, phone, appointmentsId, role);
        this.birthDate = birthDate;
    }
}
