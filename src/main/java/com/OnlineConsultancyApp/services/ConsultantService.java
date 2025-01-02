package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.User;
import com.OnlineConsultancyApp.repositories.ConsultantRepository;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.OnlineConsultancyApp.security.JwtGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    ConsultantRepository consultantRepository = new ConsultantRepository();

    @Autowired
    RedisService redisService;

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
            redisService.put(consultant);
        }
    }

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

    public void updateAvailableTime(List<Map<String, String>> availableTime, String token) throws SQLException, JsonProcessingException {
        long userId = JwtDecoder.decodedUserId(token);
        String dates = new ObjectMapper().writeValueAsString(availableTime);
        consultantRepository.updateDates(dates, userId);

    }
    public void updateAvailableTime(String availableTime, long userId) throws SQLException, JsonProcessingException {
        consultantRepository.updateDates(availableTime, userId);
    }

    public List<Consultant> getConsultantsWithFilters(double minPrice, double maxPrice, String speciality, Categories category, LocalDate date) throws SQLException, JsonProcessingException {
        List<Consultant> consultantList = new ArrayList<>();
        if(category.equals(Categories.ALL)){
            consultantList = consultantRepository.getConsultantsWithHourlyRateFilter(minPrice, maxPrice, speciality);
        }else{
            consultantList = consultantRepository.getConsultantsWithHourlyRateAndCategoryFilter(minPrice, maxPrice, speciality, category);
        }
        if(date != null){
            return getConsultantsByDate(consultantList, date);
        }
        return consultantList;
    }

    public List<Consultant> getConsultantsByDate(List<Consultant> consultantList, LocalDate date) throws JsonProcessingException {
        List<Consultant> newConsultantList = new ArrayList<>();
        for(Consultant c : consultantList){

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> availableTimeList = objectMapper.readValue(
                    c.getAvailableTime(),
                    new TypeReference<List<Map<String, String>>>() {}
            );
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for(Map<String, String> m : availableTimeList){
                LocalDate dateToCompare = LocalDate.parse(m.get("date").split(" ")[0], formatter);
                LocalDate selectedDate = LocalDate.parse(String.valueOf(date), formatter);
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

}
