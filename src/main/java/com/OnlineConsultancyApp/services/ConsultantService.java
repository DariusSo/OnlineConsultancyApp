package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.Exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.Exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.Exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.User;
import com.OnlineConsultancyApp.repositories.ConsultantRepository;
import com.OnlineConsultancyApp.security.JwtGenerator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ConsultantService {

    @Autowired
    ConsultantRepository consultantRepository;

    public void registerConsultant(Consultant consultant) throws SQLException {
        try{
            consultantRepository.getConsultantByEmail(consultant.getEmail());
            throw new UserAlreadyExistsException();
        }catch (NoSuchUserException e){
            String hashedPassword = BCrypt.hashpw(consultant.getPassword(), BCrypt.gensalt());
            consultant.setPassword(hashedPassword);
            consultant.setRole(Roles.CONSULTANT);
            consultantRepository.registerConsultant(consultant);
        }
    }

    public String authenticateConsultant(String email, String password) throws SQLException {
        User client = consultantRepository.getAuthUser(email);
        boolean authenticated = BCrypt.checkpw(password, client.getPassword());
        if (authenticated) {
            return JwtGenerator.generateJwt(client.getId(), Roles.CONSULTANT);
        } else {
            throw new BadEmailOrPasswordException();
        }
    }
}
