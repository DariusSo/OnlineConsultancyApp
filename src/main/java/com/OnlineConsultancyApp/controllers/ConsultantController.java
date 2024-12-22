package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.Exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consultant")
public class ConsultantController {

    @Autowired
    ConsultantService consultantService;

    @GetMapping("/newest")
    public ResponseEntity<List<Consultant>> getNewestConsultants(){
        try{
            List<Consultant> consultantList = consultantService.getNewestConsultants();
            return new ResponseEntity<>(consultantList, HttpStatus.OK);
        } catch (SQLException e){
            return new ResponseEntity<>(new ArrayList<Consultant>(), HttpStatus.BAD_GATEWAY);
        }
    }
    @GetMapping
    public ResponseEntity<Consultant> getConsultantById(@RequestHeader("Authorization") String jwtToken){
        try{
            Consultant consultant = consultantService.getConsultantById(jwtToken);
            return new ResponseEntity<>(consultant, HttpStatus.OK);
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>(new Consultant(), HttpStatus.NOT_FOUND);
        } catch (SQLException e) {
            return new ResponseEntity<>(new Consultant(), HttpStatus.BAD_GATEWAY);
        }
    }
    @PutMapping("/dates")
    public ResponseEntity<String> updateAvailableTime(@RequestBody List<Map<String, String>> availableTime,
                                                      @RequestHeader("Authorization") String jwtToken){
        try{
            consultantService.updateAvailableTime(availableTime, jwtToken);
            return new ResponseEntity<>("Updated!", HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database problems.", HttpStatus.BAD_GATEWAY);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return  new ResponseEntity<>("Json parse problems.", HttpStatus.BAD_REQUEST);
        }
    }


}
