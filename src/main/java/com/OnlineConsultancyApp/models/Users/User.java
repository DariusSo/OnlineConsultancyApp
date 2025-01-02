package com.OnlineConsultancyApp.models.Users;

import com.OnlineConsultancyApp.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class User {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private List<Long> appointmentsId;
    private Roles role;

    public User(long id, String firstName, String lastName, String email, String phone, List<Long> appointmentsId, Roles role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.appointmentsId = appointmentsId;
        this.role = role;
    }
}
