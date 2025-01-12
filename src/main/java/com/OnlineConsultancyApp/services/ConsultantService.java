package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.Utilities;
import com.OnlineConsultancyApp.exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.models.Users.User;
import com.OnlineConsultancyApp.repositories.ConsultantRepository;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.OnlineConsultancyApp.security.JwtGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ConsultantService {

    @Autowired
    ConsultantRepository consultantRepository;

    @Autowired
    RedisCacheService redisCacheService;
    //Registering and caching on redis
    public void registerConsultant(Consultant consultant) throws SQLException, IOException, ClassNotFoundException {
        try{
            consultantRepository.getConsultant(consultant.getEmail());
            throw new UserAlreadyExistsException();

        }catch (NoSuchUserException e){
            String hashedPassword = BCrypt.hashpw(consultant.getPassword(), BCrypt.gensalt());
            consultant.setPassword(hashedPassword);
            consultant.setRole(Roles.CONSULTANT);
            consultant.setAvailableTime("[]");
            long id = consultantRepository.registerConsultant(consultant);
            consultant.setId(id);
            redisCacheService.put(consultant);
        }
    }

    public void editConsultant(Consultant consultant, String token) throws SQLException, IOException, ClassNotFoundException {
        long id = JwtDecoder.decodedUserId(token);
        consultantRepository.editConsultant(consultant, id);
        dealWithRedisCache(id, consultant);
    }

    //to update info in cached consultants
    public void dealWithRedisCache(long id, Consultant consultant) throws IOException, ClassNotFoundException, SQLException {
        List<Consultant> consultantList = redisCacheService.getNewConsultants();
        int i = 0;
        int j = -1;
        for(Consultant c : consultantList){
            if(c.getId() == id){
                j = i;
            }
            i++;
        }
        if(j > -1){
            redisCacheService.editConsultantInCache(consultant, j);
        }
    }

    //Checking credentials and generating jwt token
    public String authenticateConsultant(String email, String password) throws SQLException {
        User client = consultantRepository.getAuthUser(email);
        boolean authenticated = BCrypt.checkpw(password, client.getPassword());
        if (authenticated) {
            return JwtGenerator.generateJwt(client.getId(), Roles.CONSULTANT);
        } else {
            throw new BadEmailOrPasswordException();
        }
    }

    public List<Consultant> getNewestConsultants() throws SQLException, JsonProcessingException {
        return consultantRepository.getNewestConsultants();
    }

    public Consultant getConsultantById(String token) throws SQLException, JsonProcessingException {
        long id = JwtDecoder.decodedUserId(token);
        return consultantRepository.getConsultant(id);
    }

    public Consultant getConsultantById(long id) throws SQLException, JsonProcessingException {
        return consultantRepository.getConsultant(id);
    }
    //For updating straight from controller
    public void updateAvailableTime(List<Map<String, String>> availableTime, String token) throws SQLException, JsonProcessingException {
        long userId = JwtDecoder.decodedUserId(token);
        String dates = Utilities.serializeToString(availableTime);
        consultantRepository.updateDates(dates, userId);

    }
    //Search method
    public List<Consultant> getConsultantsWithFilters(double minPrice, double maxPrice, String speciality, Categories category, LocalDate date) throws SQLException, JsonProcessingException {
        List<Consultant> consultantList = new ArrayList<>();
        if(category.equals(Categories.ALL)){
            consultantList = consultantRepository.getConsultantsWithoutCategory(minPrice, maxPrice, speciality);
        }else{
            consultantList = consultantRepository.getConsultantsWithCategory(minPrice, maxPrice, speciality, category);
        }
        //If we are getting date we need to deserialize string and check who is available
        if(date != null){
            return getConsultantsByDate(consultantList, date);
        }
        return consultantList;
    }
    //For getting availability by date
    public List<Consultant> getConsultantsByDate(List<Consultant> consultantList, LocalDate date) throws JsonProcessingException {
        List<Consultant> newConsultantList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Iterate through consultants which are found by other search parameters
        for(Consultant c : consultantList){
            //Deserialized time list
            List<Map<String, String>> availableTimeList = Utilities.deserializeAvailableTime(c.getAvailableTime());
            //Iterate through time list
            for(Map<String, String> m : availableTimeList){
                //Setting dates
                LocalDate dateToCompare = LocalDate.parse(m.get("date").split(" ")[0], formatter);
                LocalDate selectedDate = LocalDate.parse(String.valueOf(date), formatter);
                //Comparing
                if(dateToCompare.equals(selectedDate)){
                    newConsultantList.add(c);
                    break;
                }
            }
        }
        return newConsultantList;
    }

    public List<Consultant> getConsultantsByCategory(Categories category) throws SQLException {
        return consultantRepository.getConsultantsByCategory(category);
    }

    public String getDates(long id) throws SQLException {
        return consultantRepository.getDates(id);
    }
    //For updating from other services
    public void updateAvailableTime(String availableTime, long userId) throws SQLException, JsonProcessingException {
        consultantRepository.updateDates(availableTime, userId);
    }
    public List<Consultant> getConsultantList() throws SQLException {
        return consultantRepository.getConsultantList();
    }

}
