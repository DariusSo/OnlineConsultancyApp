package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.exceptions.NoAccessException;
import com.OnlineConsultancyApp.exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.User;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.OnlineConsultancyApp.services.AppointmentService;
import com.OnlineConsultancyApp.services.ClientService;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<String> createAppointment(@RequestBody Appointment appointment,
                                                    @RequestHeader("Authorization") String jwtToken){
        try{
            appointmentService.addAppointment(appointment, jwtToken);
            return new ResponseEntity<>("Appointment created", HttpStatus.OK);
        }catch (SQLException e){
            e.printStackTrace();
            return new ResponseEntity<>("Errors in database", HttpStatus.BAD_REQUEST);
        } catch (MalformedJwtException e){
            return new ResponseEntity<>("You need to login", HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Unknown error", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointments(@RequestHeader("Authorization") String jwtToken){
        try{
            List<Appointment> appointmentList = appointmentService.findAppointments(jwtToken);
            return new ResponseEntity<>(appointmentList, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_GATEWAY);
        } catch (ThereIsNoSuchRoleException e){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping
    public ResponseEntity<String> confirmAppointment(@RequestHeader("Authorization") String jwtToken, long appointmentId){
        try{
            appointmentService.confirmAppointment(jwtToken, appointmentId);
            return new ResponseEntity<>("Appointment confirmed", HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Database problems", HttpStatus.BAD_GATEWAY);
        } catch (NoAccessException e){
            return new ResponseEntity<>("No access", HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token, long appointmentId){
        try{
            User user = appointmentService.getUserInfo(token, appointmentId);
            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (SQLException | JsonProcessingException e) {
            return new ResponseEntity<>(new Client(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoAccessException e){
            return new ResponseEntity<>(new Client(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            return new ResponseEntity<>(new Client(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/connect")
    public boolean connectToAppointment(UUID roomUuid) throws SQLException {
        if(appointmentService.connectToAppointment(roomUuid)){
            return true;
        }else{
            return false;
        }
    }
}
