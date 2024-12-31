package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.exceptions.NoAccessException;
import com.OnlineConsultancyApp.exceptions.NoSuchAppointmentException;
import com.OnlineConsultancyApp.exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.EmailMessage;
import com.OnlineConsultancyApp.repositories.AppointmentRepository;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Autowired
    AuthService authService;
    @Autowired
    StripeService stripeService;


    public void addAppointment(Appointment appointment, String token) throws Exception {
        UUID roomUuid = UUID.randomUUID();
        //Add appointment
        long userId = JwtDecoder.decodedUserId(token);
        appointment.setUserId(userId);
        appointment.setRoomUuid(roomUuid);
        appointmentRepository.addAppointment(appointment);
        //Add appointment id to client
        long appointmentId = appointmentRepository.getAppointmentId(userId, appointment.getConsultantId());
        clientService.addAppointment(userId, appointmentId);
        //Get participants
        Client client = clientService.getClientById(userId);
        Consultant consultant = consultantService.getConsultantById(appointment.getConsultantId());
        //Remove available time from consultant
        String updatedDateAndTimeList = removeDateFromList(consultant.getAvailableTime(), appointment.getTimeAndDate());
        consultantService.updateAvailableTime(updatedDateAndTimeList, consultant.getId());
        deleteAppointmentAfter5min(appointmentId, consultant.getAvailableTime(), appointment.getTimeAndDate(), consultant.getId());
        //Create email
        String clientMessage = "Appointment with " + consultant.getFirstName() + " " + consultant.getLastName() + " created. Waiting for approval.";
        String consultantMessage = client.getFirstName() + " " + client.getLastName() + " created an appointment with you, please confirm.";
        EmailMessage clientEmail = new EmailMessage(client.getEmail(), clientMessage);
        EmailMessage consultantEmail = new EmailMessage(consultant.getEmail(), consultantMessage);
        //Send
        rabbitMQService.sendConfirmationEmail(clientEmail);
        rabbitMQService.sendConfirmationEmail(consultantEmail);

    }

    public void deleteAppointmentAfter5min(long id, String availableTimeString, LocalDateTime appointmentDateAndTime, long consultantId){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30 * 1000);
                    Appointment appointment = appointmentRepository.getAppointmentsByAppointmenttId(id);
                    if(!appointment.isPaid()){
                        deleteAppointment(id, availableTimeString, appointmentDateAndTime, consultantId);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new NoSuchAppointmentException();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();

    }

    public List<Appointment> findAppointments(String jwtToken) throws SQLException {
        Roles role = JwtDecoder.decodedRole(jwtToken);
        long userId = JwtDecoder.decodedUserId(jwtToken);
        if(role == Roles.CLIENT){
            List<Appointment> appointmentList = appointmentRepository.getAppointmentsByUserId(userId);
            return appointmentList;
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
    public String removeDateFromList(String availableTimeString, LocalDateTime appointmentDateAndTime) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, String>> availableTimeList = objectMapper.readValue(
                availableTimeString,
                new TypeReference<List<Map<String, String>>>() {}
        );

        List<Map<String, String>> newList = new ArrayList<>();

        for (Map<String, String> availableTime : availableTimeList) {
            String dateStr = availableTime.get("date");
            if (dateStr != null) {

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime freeTime = LocalDateTime.parse(dateStr, dateTimeFormatter);

                if (!freeTime.equals(appointmentDateAndTime)) {
                    newList.add(availableTime);

                }
            }
        }
        return objectMapper.writeValueAsString(newList);
    }

    public void cancelAppointment(String token, long appointmentId) throws StripeException, SQLException {
        authService.authenticateRole(token);
        stripeService.createRefund(appointmentId);
    }

    public void deleteAppointment(long id, String availableTimeString, LocalDateTime appointmentDateAndTime, long consultantId) throws SQLException, JsonProcessingException {
        appointmentRepository.deleteAppointment(id);
        String dateString = addDateTolist(availableTimeString, appointmentDateAndTime);
        consultantService.updateAvailableTime(dateString, consultantId);
    }

    public String addDateTolist(String availableTimeString, LocalDateTime appointmentDateAndTime) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, String>> availableTimeList = objectMapper.readValue(
                availableTimeString,
                new TypeReference<List<Map<String, String>>>() {}
        );

        Map<String, String> dateToAdd = new HashMap<>();
        dateToAdd.put("date", String.valueOf(appointmentDateAndTime));


        return objectMapper.writeValueAsString(availableTimeList);
    }
    public Appointment getByRoomUuid(UUID uuid) throws SQLException {
        return appointmentRepository.getAppointmentsRoomUuid(uuid);
    }
    public void addStripeSessionId(String sessionId, UUID uuid) throws SQLException {
        appointmentRepository.addStripeSessionId(sessionId, uuid);
    }
    public String getStripeSessionId(long appointmentId) throws SQLException {
        return appointmentRepository.getStripeSessionId(appointmentId);
    }
    public Appointment getAppointmentById(long id) throws SQLException {
        return appointmentRepository.getAppointmentsByAppointmenttId(id);
    }


}
