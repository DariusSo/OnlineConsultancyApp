package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.services.AppointmentService;
import com.OnlineConsultancyApp.services.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    ClientService clientService;

    @PutMapping("/edit")
    public ResponseEntity<String> editClient(@RequestHeader("Authorization") String token, @RequestBody Client client){
        try{
            clientService.editClient(token, client);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Problems.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
