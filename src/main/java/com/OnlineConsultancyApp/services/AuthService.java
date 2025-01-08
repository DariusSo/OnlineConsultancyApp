package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.exceptions.BadRolesException;
import com.OnlineConsultancyApp.exceptions.NoAccessException;
import com.OnlineConsultancyApp.exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Messages.ForumMessage;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.models.Users.User;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    ClientService clientService;
    @Autowired
    ConsultantService consultantService;
    @Autowired
    AppointmentService appointmentService;

    //For login
    public String authenticate(User user) throws SQLException, BadRequestException {
        if(user instanceof Client){
            return clientService.authenticateClient(user.getEmail(), user.getPassword());
        } else if (user instanceof Consultant) {
            return consultantService.authenticateConsultant(user.getEmail(), user.getPassword());
        }else{
            throw new BadRequestException();
        }
    }
    //For getting logged in users info
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
    //For authenticating appointment room access
    public void authenticate(String token, UUID roomUuid) throws SQLException {
        long userId = JwtDecoder.decodedUserId(token);
        Roles role = JwtDecoder.decodedRole(token);
        Appointment appointment = appointmentService.getByRoomUuid(roomUuid);
        checkAuth(role, userId, appointment);
    }
    //For authenticating appointment access
    public void checkAuth(Roles role, long userId, Appointment appointment){
        if(role == Roles.CLIENT){
            if(appointment.getUserId() == userId){

            }else{
                throw new NoAccessException();
            }
        } else if (role == Roles.CONSULTANT) {
            if(appointment.getConsultantId() == userId){

            }else{
                throw new NoAccessException();
            }
        }else{
            throw new ThereIsNoSuchRoleException();
        }
    }
    //Authenticating role for refunds
    public void authenticateRole(String token) throws NoAccessException{
        Roles role = JwtDecoder.decodedRole(token);
        if(role == Roles.CLIENT || role == Roles.CONSULTANT){

        }else{
            throw new NoAccessException();
        }
    }
    public void authenticate(String token, long consultantIdFromMessage){
        long consultantId = JwtDecoder.decodedUserId(token);
        Roles role = JwtDecoder.decodedRole(token);
        if(consultantIdFromMessage != consultantId || role != Roles.CONSULTANT){
            throw new NoAccessException();
        }
    }

    public void authenticateClientRole(String token){
        Roles role = JwtDecoder.decodedRole(token);
        if(role == Roles.CONSULTANT){
            throw new BadRolesException();
        }
    }
}
