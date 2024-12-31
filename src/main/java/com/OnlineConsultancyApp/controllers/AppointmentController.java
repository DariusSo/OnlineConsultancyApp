package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.exceptions.NoAccessException;
import com.OnlineConsultancyApp.exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.services.AppointmentService;
import com.stripe.exception.StripeException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    @GetMapping("/cancel")
    public ResponseEntity<String> cancelAppointment(@RequestHeader("Authorization") String token, long appointmentId){
        try{
            appointmentService.cancelAppointment(token, appointmentId);
            return new ResponseEntity<>("Success, appointment canceled.", HttpStatus.OK);
        } catch (StripeException | SQLException e) {
            return new ResponseEntity<>("Stripe or SQL", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoAccessException e){
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }

}
