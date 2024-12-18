package com.OnlineConsultancyApp.models;

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
    private String password;
    private long[] appointmentId;
    private String role;
}
