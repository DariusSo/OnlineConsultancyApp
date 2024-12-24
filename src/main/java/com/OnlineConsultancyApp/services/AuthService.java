package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.Exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.User;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class AuthService {

    @Autowired
    ClientService clientService;
    @Autowired
    ConsultantService consultantService;


    public String authenticate(User user) throws SQLException, BadRequestException {
        if(user instanceof Client){
            return clientService.authenticateClient(user.getEmail(), user.getPassword());
        } else if (user instanceof Consultant) {
            return consultantService.authenticateConsultant(user.getEmail(), user.getPassword());
        }else{
            throw new BadRequestException();
        }
    }
    public User getProfileInfo(String jwtToken) throws SQLException, JsonProcessingException {
        long id = JwtDecoder.decodedUserId(jwtToken);
        Roles role = JwtDecoder.decodedRole(jwtToken);
        if(role == Roles.CLIENT){
            return clientService.getClientById(id);
        } else if (role == Roles.CONSULTANT) {
            return consultantService.getConsultantById(id);
        } else {
            throw new ThereIsNoSuchRoleException();
        }
    }
}
