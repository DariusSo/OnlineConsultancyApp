package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.exceptions.*;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.User;
import com.OnlineConsultancyApp.services.AuthService;
import com.OnlineConsultancyApp.services.ClientService;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.UUID;

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
            e.printStackTrace();
            return new ResponseEntity<>("Problems with database.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("Converting appointments", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login/client")
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

    @PostMapping("/login/consultant")
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
    @CacheEvict(value = "consultant_search", allEntries = true)
    public ResponseEntity<String> registration(@RequestBody Consultant consultant){
        try{
            consultantService.registerConsultant(consultant);
            return new ResponseEntity<>("Registration successful!", HttpStatus.OK);
        }catch (UserAlreadyExistsException e){
            return new ResponseEntity<>("User with this email already exists.", HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Problems with database.", HttpStatus.BAD_GATEWAY);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Unexpected error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfileInfo(@RequestHeader("Authorization") String jwtToken) throws SQLException {
        try{
            User user = authService.getProfileInfo(jwtToken);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ThereIsNoSuchRoleException e){
            return new ResponseEntity<>(new Client(), HttpStatus.BAD_REQUEST);
        } catch (SQLException | JsonProcessingException e){
            return new ResponseEntity<>(new Client(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @GetMapping("/consultationRoom")
    public ResponseEntity<String> authenticateConsultationRoom(@RequestHeader("Authorization") String jwtToken, UUID roomUuid){
        try{
            authService.authenticate(jwtToken, roomUuid);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database problems", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoAccessException e){
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        } catch (ThereIsNoSuchRoleException e){
            return new ResponseEntity<>("Bad role", HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
