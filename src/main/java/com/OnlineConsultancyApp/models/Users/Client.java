package com.OnlineConsultancyApp.models.Users;

import com.OnlineConsultancyApp.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client extends User {

    private LocalDate birthDate;

    public Client(long id, String firstName, String lastName, String email, String phone, List<Long> appointmentsId, Roles role, LocalDate birthDate) {
        super(id, firstName, lastName, email, phone, appointmentsId, role);
        this.birthDate = birthDate;
    }
}
