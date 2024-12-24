package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.Exceptions.NoAccessException;
import com.OnlineConsultancyApp.Exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.EmailMessage;
import com.OnlineConsultancyApp.repositories.AppointmentRepository;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    ConsultantService consultantService;
    @Autowired
    ClientService clientService;
    @Autowired
    RabbitMQService rabbitMQService;


    public void addAppointment(Appointment appointment, String token) throws Exception {
        //Add appointment
        long userId = JwtDecoder.decodedUserId(token);
        appointment.setUserId(userId);
        appointmentRepository.addAppointment(appointment);
        //Add appointment id to client
        long appointmentId = appointmentRepository.getAppointmentId(userId, appointment.getConsultantId());
        clientService.addAppointment(userId, appointmentId);

        Client client = clientService.getClientById(userId);
        Consultant consultant = consultantService.getConsultantById(appointment.getConsultantId());
        //Create email
        String clientMessage = "Appointment with " + consultant.getFirstName() + " " + consultant.getLastName() + "created. Waiting for approval.";
        String consultantMessage = client.getFirstName() + " " + client.getLastName() + "created an appointment with you, please confirm.";
        EmailMessage clientEmail = new EmailMessage(client.getEmail(), clientMessage);
        EmailMessage consultantEmail = new EmailMessage(consultant.getEmail(), consultantMessage);
        //Send
        rabbitMQService.sendConfirmationEmail(clientEmail);
        rabbitMQService.sendConfirmationEmail(consultantEmail);

    }

    public List<Appointment> findAppointments(String jwtToken) throws SQLException {
        Roles role = JwtDecoder.decodedRole(jwtToken);
        long userId = JwtDecoder.decodedUserId(jwtToken);
        if(role == Roles.CLIENT){
            return appointmentRepository.getAppointmentsByUserId(userId);
        } else if (role == Roles.CONSULTANT) {
            return appointmentRepository.getAppointmentsByConsultantId(userId);
        }else{
            throw new ThereIsNoSuchRoleException();
        }
    }
    public void confirmAppointment(String jwtToken, long appointmentId) throws SQLException {
        Appointment appointment = appointmentRepository.getAppointmentsByAppointmenttId(appointmentId);
        Roles role = JwtDecoder.decodedRole(jwtToken);
        long userId = JwtDecoder.decodedUserId(jwtToken);
        if(role == Roles.CONSULTANT && appointment.getConsultantId() == userId){
            appointmentRepository.confirmAppointment(appointmentId);
        }else{
            throw new NoAccessException();
        }
    }

    public void updatePaymentStatus(UUID uuid) throws SQLException {
        appointmentRepository.updatePaidStatus(uuid);
    }

}
