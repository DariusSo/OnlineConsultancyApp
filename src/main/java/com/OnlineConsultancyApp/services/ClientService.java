package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.User;
import com.OnlineConsultancyApp.repositories.ClientRepository;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.OnlineConsultancyApp.security.JwtGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;
    //Adding to db and password hashing
    public void registerClient(Client client) throws SQLException, JsonProcessingException {
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
    //Checking credentials and generating jwt token
    public String authenticateClient(String email, String password) throws SQLException {
        User client = clientRepository.getAuthUser(email);
        boolean authenticated = BCrypt.checkpw(password, client.getPassword());
        if(authenticated){
            return JwtGenerator.generateJwt(client.getId(), Roles.CLIENT);
        }else{
            throw new BadEmailOrPasswordException();
        }
    }

    public Client getClientById(long id) throws SQLException, JsonProcessingException {
        return clientRepository.getClientById(id);
    }

    public void editClient(String token, Client client) throws SQLException {
        long userId = JwtDecoder.decodedUserId(token);
        clientRepository.editClient(client, userId);
    }
}
