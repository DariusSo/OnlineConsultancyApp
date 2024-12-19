package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.Exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.Exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.Exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.User;
import com.OnlineConsultancyApp.services.AuthService;
import com.OnlineConsultancyApp.services.ClientService;
import com.OnlineConsultancyApp.services.ConsultantService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    ClientService clientService;
    @Autowired
    ConsultantService consultantService;
    @Autowired
    AuthService authService;


    @PostMapping("/client")
    public ResponseEntity<String> registration(@RequestBody Client client){
        try{
            clientService.registerClient(client);
            return new ResponseEntity<>("Registration successful!", HttpStatus.OK);
        } catch (UserAlreadyExistsException e){
            return new ResponseEntity<>("User with this email already exists.", HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            return new ResponseEntity<>("Problems with database.", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/client")
    public ResponseEntity<String> login(@RequestBody Client client){
        try{
            String token = authService.authenticate(client);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (BadEmailOrPasswordException | NoSuchUserException e){
            return new ResponseEntity<>("Bad email or password.", HttpStatus.UNAUTHORIZED);
        } catch (SQLException e){
            return new ResponseEntity<>("Problems with database.", HttpStatus.BAD_GATEWAY);
        } catch (BadRequestException e) {
            return new ResponseEntity<>("Bad parameters", HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>("Unexpected error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/consultant")
    public ResponseEntity<String> login(@RequestBody Consultant consultant){
        try{
            String token = authService.authenticate(consultant);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (BadEmailOrPasswordException e){
            return new ResponseEntity<>("Bad email or password.", HttpStatus.UNAUTHORIZED);
        } catch (SQLException e){
            return new ResponseEntity<>("Problems with database.", HttpStatus.BAD_GATEWAY);
        } catch (BadRequestException e) {
            return new ResponseEntity<>("Bad parameters", HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>("Unexpected error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/consultant")
    public ResponseEntity<String> registration(@RequestBody Consultant consultant){
        try{
            consultantService.registerConsultant(consultant);
            return new ResponseEntity<>("Registration successful!", HttpStatus.OK);
        }catch (UserAlreadyExistsException e){
            return new ResponseEntity<>("User with this email already exists.", HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            return new ResponseEntity<>("Problems with database.", HttpStatus.BAD_GATEWAY);
        }catch (Exception e){
            return new ResponseEntity<>("Unexpected error", HttpStatus.BAD_REQUEST);
        }
    }


}
