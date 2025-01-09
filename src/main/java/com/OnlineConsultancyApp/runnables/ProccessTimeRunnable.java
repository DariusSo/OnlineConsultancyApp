package com.OnlineConsultancyApp.runnables;

import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.OnlineConsultancyApp.runnables.CheckTimesLeftRunnable.*;

@Service
public class ProccessTimeRunnable implements Runnable{

    @Autowired
    ConsultantService consultantService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run() {

        for(Consultant consultant : consultantList){
            reentrantLock.lock();
            if(processedConsultantsIds.contains(consultant.getId())){
                reentrantLock.unlock();
            }else{
                try {
                    processedConsultantsIds.add(consultant.getId());
                    reentrantLock.unlock();
                    LocalDateTime now = LocalDateTime.now();
                    List<Map<String, String>> updatedList = new ArrayList<>();
                    List<Map<String, String>> availableTimeList = objectMapper.readValue(
                            consultant.getAvailableTime(),
                            new TypeReference<List<Map<String, String>>>() {}
                    );
                    for(Map<String, String> dateTime : availableTimeList){
                        String dateTimeString = dateTime.get("date");
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime dateTimeInList = LocalDateTime.parse(dateTimeString, dateTimeFormatter);
                        LocalDateTime dateTimeMinusTwelve = dateTimeInList.minusHours(12);
                        if (dateTimeMinusTwelve.isAfter(now)){
                            updatedList.add(dateTime);
                        }
                    }
                    String updatedDateTimeString = objectMapper.writeValueAsString(updatedList);
                    consultantService.updateAvailableTime(updatedDateTimeString, consultant.getId());
                    System.out.println(Thread.currentThread().getName() + " Consultant: " + consultant.getId());

                } catch (JsonProcessingException | SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }
}
