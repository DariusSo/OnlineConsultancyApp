package com.OnlineConsultancyApp;

import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.models.Users.User;
import com.OnlineConsultancyApp.repositories.ClientRepository;
import com.OnlineConsultancyApp.services.AppointmentService;
import com.OnlineConsultancyApp.services.ClientService;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AppointmentServiceTests {

    @Mock
    ClientService clientService;
    @Mock
    ConsultantService consultantService;
    @InjectMocks
    AppointmentService appointmentService;


    @Test
    //Test if method adds date correctly to string
    void testAddDateToList() throws JsonProcessingException {
        // Arrange
        AppointmentService appointmentService = new AppointmentService();
        String availableTime = "[{\"date\":\"2025-01-14 15:00\"},{\"date\":\"2025-01-14 17:00\"}]";
        String dateStr = "2025-02-15T17:00";
        String expectedDateList = "[{\"date\":\"2025-01-14 15:00\"},{\"date\":\"2025-01-14 17:00\"},{\"date\":\"2025-02-15 17:00\"}]";

        // Act
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime dateTimeToAdd = LocalDateTime.parse(dateStr, formatter);
        String updatedTimeList = appointmentService.addDateTolist(availableTime, dateTimeToAdd);

        // Assert
        assertEquals(expectedDateList, updatedTimeList);
    }

    @Test
    //Test if method removes date correctly from string
    void testRemoveDateFromList() throws JsonProcessingException {
        // Arrange
        AppointmentService appointmentService = new AppointmentService();
        String availableTimeString = "[{\"date\":\"2025-01-14 15:00\"},{\"date\":\"2025-01-14 17:00\"},{\"date\":\"2025-02-15 17:00\"}]";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateToRemove = LocalDateTime.parse("2025-01-14 17:00", formatter);
        String expectedAvailableTimeString = "[{\"date\":\"2025-01-14 15:00\"},{\"date\":\"2025-02-15 17:00\"}]";

        // Act
        String updatedTimeString = appointmentService.removeDateFromList(availableTimeString, dateToRemove);

        // Assert
        assertEquals(expectedAvailableTimeString, updatedTimeString);
    }

    @Test
    //Test if user getting correct other person for appointment
    void testReturnUserByRole() throws SQLException, JsonProcessingException {
        //Arrange
        MockitoAnnotations.openMocks(this);

        Client userClient = new Client();
        userClient.setId(1);
        userClient.setRole(Roles.CLIENT);

        Consultant userConsultant = new Consultant();
        userConsultant.setId(1);
        userConsultant.setRole(Roles.CONSULTANT);

        Appointment appointment = new Appointment();
        appointment.setUserId(1);
        appointment.setConsultantId(1);

        Mockito.when(clientService.getClientById(1)).thenReturn(userClient);
        Mockito.when(consultantService.getConsultantById(1)).thenReturn(userConsultant);

        //Act
        User tryClient = appointmentService.returnUserByRole(1L, Roles.CLIENT, appointment);
        User tryConsultant = appointmentService.returnUserByRole(1L, Roles.CONSULTANT, appointment);

        //Assert
        Assertions.assertEquals(tryClient.getRole(), Roles.CONSULTANT);
        Assertions.assertEquals(tryConsultant.getRole(), Roles.CLIENT);
        Mockito.verify(clientService, Mockito.times(1)).getClientById(1);
        Mockito.verify(consultantService, Mockito.times(1)).getConsultantById(1);
    }

}
