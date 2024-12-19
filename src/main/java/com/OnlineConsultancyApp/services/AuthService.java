package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.User;
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
}
