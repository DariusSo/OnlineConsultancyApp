package com.OnlineConsultancyApp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class Utilities {

//    public static final String URL = "jdbc:mysql://127.0.0.1:3306/consultancy";
//    public static final String dbUser = System.getenv("DB_USER");
//    public static final String dbPassword = System.getenv("DB_PASSWORD");


    public static List<Map<String, String>> deserializeAvailableTime(String availableTime) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(
                availableTime,
                new TypeReference<List<Map<String, String>>>() {}
        );
    }
    public static String serializeToString(List<Map<String, String>> object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }


}
