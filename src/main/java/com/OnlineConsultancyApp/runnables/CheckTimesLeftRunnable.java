package com.OnlineConsultancyApp.runnables;

import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CheckTimesLeftRunnable implements Runnable {

    public static List<Consultant> consultantList = new ArrayList<>();
    public static List<Long> processedConsultantsIds = new ArrayList<>();
    public static ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public void run() {
        while(true){
            try {
                getConsultantList();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            CountDownLatch countDownLatch = new CountDownLatch(4);

            Thread thread1 = new Thread(new ProccessTimeRunnable(countDownLatch));
            Thread thread2 = new Thread(new ProccessTimeRunnable(countDownLatch));
            Thread thread3 = new Thread(new ProccessTimeRunnable(countDownLatch));
            Thread thread4 = new Thread(new ProccessTimeRunnable(countDownLatch));

            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("List completed.");
            System.out.println(processedConsultantsIds.size());
            try {
                Thread.sleep(3600 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processedConsultantsIds = new ArrayList<>();
            consultantList = new ArrayList<>();
        }
    }

    public static void getConsultantList() throws SQLException {
        ObjectMapper objectMapper = new ObjectMapper();
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM consultants");

        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
            Consultant consultant = new Consultant(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("email"), rs.getString("phone"), appointmentList, Roles.valueOf(rs.getString("role")),
                    rs.getString("categories"), rs.getString("available_time"), rs.getString("speciality"),
                    rs.getString("description"), rs.getBigDecimal("hourly_rate"));
            consultantList.add(consultant);
        }
    }
}
