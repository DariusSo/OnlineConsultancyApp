package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.User;
import com.OnlineConsultancyApp.repositories.ClientRepository;
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

    public void addAppointment(long id, long appointmentId) throws SQLException, JsonProcessingException {
        clientRepository.addAppointment(id, appointmentId);
    }
}
