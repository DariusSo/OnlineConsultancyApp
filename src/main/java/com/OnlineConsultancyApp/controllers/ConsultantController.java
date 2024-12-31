package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.OnlineConsultancyApp.services.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consultant")
public class ConsultantController {

    @Autowired
    ConsultantService consultantService;

    @Autowired
    RedisService redisService;

    @GetMapping("/newest")
    public ResponseEntity<List<Consultant>> getNewestConsultants(){
        try{
            List<Consultant> consultantList = redisService.getNewConsultants();
            return new ResponseEntity<>(consultantList, HttpStatus.OK);
        } catch (JsonProcessingException e){
            return new ResponseEntity<>(new ArrayList<Consultant>(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<Consultant> getConsultantById(@RequestHeader("Authorization") String jwtToken){
        try{
            Consultant consultant = consultantService.getConsultantById(jwtToken);
            return new ResponseEntity<>(consultant, HttpStatus.OK);
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>(new Consultant(), HttpStatus.NOT_FOUND);
        } catch (SQLException | JsonProcessingException e) {
            return new ResponseEntity<>(new Consultant(), HttpStatus.BAD_GATEWAY);
        } catch (MalformedJwtException e){
            return new ResponseEntity<>(new Consultant(), HttpStatus.UNAUTHORIZED);
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
//    @GetMapping("/search")
//    public ResponseEntity<List<Consultant>> searchConsultants(double minPrice, double maxPrice, String speciality, Categories category, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
//        try {
//            List<Consultant> consultantList = consultantService.getConsultantsWithFilters(minPrice, maxPrice, speciality, category, date);
//            return new ResponseEntity<>(consultantList, HttpStatus.OK);
//        } catch (SQLException | JsonProcessingException e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @GetMapping("/search")
    @Cacheable("consultant_search")
    public List<Consultant> searchConsultants(double minPrice, double maxPrice, String speciality, Categories category, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        try {
            return consultantService.getConsultantsWithFilters(minPrice, maxPrice, speciality, category, date);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/dates")
    public ResponseEntity<String> getDates(long id){
        try{
            String dates = consultantService.getDates(id);
            return new ResponseEntity<>(dates, HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database problems", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoSuchUserException e){
            return new ResponseEntity<>("Can't find consultant", HttpStatus.BAD_REQUEST);
        }
    }


}
