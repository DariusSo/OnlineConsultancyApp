package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.Exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.Exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.Exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.User;
import com.OnlineConsultancyApp.repositories.ClientRepository;
import com.OnlineConsultancyApp.security.JwtGenerator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public void registerClient(Client client) throws SQLException {
        try{
            clientRepository.getClientByEmail(client.getEmail());
            throw new UserAlreadyExistsException();
        }catch (NoSuchUserException e){
            String hashedPassword = BCrypt.hashpw(client.getPassword(), BCrypt.gensalt());
            client.setPassword(hashedPassword);
            client.setRole(Roles.CLIENT);
            clientRepository.registerClient(client);
        }
    }

    public String authenticateClient(String email, String password) throws SQLException {
        User client = clientRepository.getAuthUser(email);
        boolean authenticated = BCrypt.checkpw(password, client.getPassword());
        if(authenticated){
            return JwtGenerator.generateJwt(client.getId());
        }else{
            throw new BadEmailOrPasswordException();
        }

    }
}
