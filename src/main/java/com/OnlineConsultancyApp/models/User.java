package com.OnlineConsultancyApp.models;

import com.OnlineConsultancyApp.enums.Roles;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String appointmentsId;
    private Roles role;

    public User(long id, String firstName, String lastName, String email, String phone, String appointmentsId, Roles role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.appointmentsId = appointmentsId;
        this.role = role;
    }
}
