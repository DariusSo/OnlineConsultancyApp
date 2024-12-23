package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.Exceptions.NoAccessException;
import com.OnlineConsultancyApp.Exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.OnlineConsultancyApp.services.AppointmentService;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

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
    public ResponseEntity<Appointment> getAppointments(@RequestHeader("Authorization") String jwtToken){
        try{
            Appointment appointment = appointmentService.findAppointments(jwtToken);
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(new Appointment(), HttpStatus.BAD_GATEWAY);
        } catch (ThereIsNoSuchRoleException e){
            return new ResponseEntity<>(new Appointment(), HttpStatus.NOT_FOUND);
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

}
