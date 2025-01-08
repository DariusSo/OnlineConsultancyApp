package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.Utilities;
import com.OnlineConsultancyApp.exceptions.NoAccessException;
import com.OnlineConsultancyApp.exceptions.NoSuchAppointmentException;
import com.OnlineConsultancyApp.exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.models.Messages.EmailMessage;
import com.OnlineConsultancyApp.models.Users.User;
import com.OnlineConsultancyApp.repositories.AppointmentRepository;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AppointmentService {

    AppointmentRepository appointmentRepository = new AppointmentRepository();
    @Autowired
    ConsultantService consultantService;
    @Autowired
    ClientService clientService;
    @Autowired
    RabbitMQService rabbitMQService;

    public void createAppointment(Appointment appointment, String token) throws Exception {
        long userId = JwtDecoder.decodedUserId(token);

        //Get participants
        Client client = clientService.getClientById(userId);
        Consultant consultant = consultantService.getConsultantById(appointment.getConsultantId());

        //Set room uuid and save appointment to db
        addAppointment(appointment, token, userId);

        //Remove available time from consultant
        updateAvailableTimes(consultant.getAvailableTime(), appointment.getTimeAndDate(), consultant.getId());

        //New thread to wait 30min for Stripe session to end and delete appointment if not paid
        long appointmentId = appointmentRepository.getAppointmentId(userId, appointment.getConsultantId());
        deleteAppointmentAfter30min(appointmentId, consultant.getAvailableTime(), appointment.getTimeAndDate(), consultant.getId());

        //Confirmation emails
        createAndSendEmail(consultant, client);
    }
    public void addAppointment(Appointment appointment, String token, long userId) throws SQLException {
        UUID roomUuid = UUID.randomUUID();

        appointment.setUserId(userId);
        appointment.setRoomUuid(roomUuid);
        appointmentRepository.addAppointment(appointment);
    }

    public void updateAvailableTimes(String availableTimes, LocalDateTime appointmentTime, long consultantId) throws JsonProcessingException, SQLException {
        String updatedDateAndTimeList = removeDateFromList(availableTimes, appointmentTime);
        consultantService.updateAvailableTime(updatedDateAndTimeList, consultantId);
    }

    public void createAndSendEmail(Consultant consultant, Client client) throws Exception {
        String clientMessage = "Appointment with " + consultant.getFirstName() + " " + consultant.getLastName() + " created. Waiting for approval.";
        String consultantMessage = client.getFirstName() + " " + client.getLastName() + " created an appointment with you, please confirm.";
        EmailMessage clientEmail = new EmailMessage(client.getEmail(), clientMessage);
        EmailMessage consultantEmail = new EmailMessage(consultant.getEmail(), consultantMessage);
        //Send
        rabbitMQService.sendConfirmationEmail(clientEmail);
        rabbitMQService.sendConfirmationEmail(consultantEmail);
    }

    public void deleteAppointmentAfter30min(long id, String availableTimeString, LocalDateTime appointmentDateAndTime, long consultantId){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //30min
                    Thread.sleep(30 * 1000);
                    //Get appointment and delete if not paid
                    Appointment appointment = appointmentRepository.getAppointmentsByAppointmenttId(id);
                    if(!appointment.isPaid()){
                        deleteAppointment(id, availableTimeString, appointmentDateAndTime, consultantId);
                    }
                } catch (SQLException e) {
                    throw new NoSuchAppointmentException();
                } catch (InterruptedException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();

    }

    public List<Appointment> findAppointments(String jwtToken) throws SQLException {
        //Get info
        Roles role = JwtDecoder.decodedRole(jwtToken);
        long userId = JwtDecoder.decodedUserId(jwtToken);
        //Check role to know which method to use
        if(role == Roles.CLIENT){
            return appointmentRepository.getAppointmentsByUserId(userId);
        } else if (role == Roles.CONSULTANT) {
            return appointmentRepository.getAppointmentsByConsultantId(userId);
        }else{
            throw new ThereIsNoSuchRoleException();
        }
    }
    //For consultant to confirm paid appointment
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
        List<Map<String, String>> availableTimeList = Utilities.deserializeAvailableTime(availableTimeString);
        List<Map<String, String>> newList = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        //Going through list and comparing dates to know which dates are still valid
        for (Map<String, String> availableTime : availableTimeList) {
            //Making dates
            String dateStr = availableTime.get("date");
            LocalDateTime freeTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
            //Comparing
            if (!freeTime.equals(appointmentDateAndTime)) {
                newList.add(availableTime);
            }
        }
        return Utilities.serializeToString(newList);
    }
    //Canceling
    public void deleteAppointment(long id, String availableTimeString, LocalDateTime appointmentDateAndTime, long consultantId) throws SQLException, JsonProcessingException {
        appointmentRepository.deleteAppointment(id);
        String dateString = addDateTolist(availableTimeString, appointmentDateAndTime);
        consultantService.updateAvailableTime(dateString, consultantId);
    }
    //When ended
    public void deleteAppointment(long id) throws SQLException, JsonProcessingException {
        appointmentRepository.deleteAppointment(id);
    }

    public String addDateTolist(String availableTimeString, LocalDateTime appointmentDateAndTime) throws JsonProcessingException {
        List<Map<String, String>> availableTimeList = Utilities.deserializeAvailableTime(availableTimeString);

        //Correcting time string
        String formattedDate = String.valueOf(appointmentDateAndTime).replace('T', ' ');

        //Add date to map
        Map<String, String> dateToAdd = new HashMap<>();
        dateToAdd.put("date", formattedDate);

        //Add map to list
        availableTimeList.add(dateToAdd);

        return Utilities.serializeToString(availableTimeList);
    }
    public User getUserInfo(String token, long appointmentId) throws SQLException, JsonProcessingException {
        //To check if user getting info for correct appointment
        Appointment appointment = getAppointmentById(appointmentId);

        //Getting necessary data
        Roles role = JwtDecoder.decodedRole(token);
        long userId = JwtDecoder.decodedUserId(token);

        return returnUserByRole(userId, role, appointment);
    }
    //Check if user is getting info for correct appointment
    public User returnUserByRole(long userId, Roles role, Appointment appointment) throws SQLException, JsonProcessingException {
        if(role == Roles.CLIENT){
            if(userId == appointment.getUserId()){
                return consultantService.getConsultantById(appointment.getConsultantId());
            }else{
                throw new NoAccessException();
            }
        } else if (role == Roles.CONSULTANT) {
            if(userId == appointment.getConsultantId()){
                return clientService.getClientById(appointment.getUserId());
            }else{
                throw new NoAccessException();
            }
        } else{
            throw new NoAccessException();
        }
    }

    public boolean connectToAppointment(UUID roomUuid) throws SQLException {
        Appointment appointment = appointmentRepository.getAppointmentsRoomUuid(roomUuid);
        long difference = Duration.between(LocalDateTime.now(), appointment.getTimeAndDate()).toMinutes();
        return difference <= 5;
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
