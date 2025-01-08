package com.OnlineConsultancyApp.runnables;

import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CheckTimesLeftRunnable implements Runnable {

    public static List<Consultant> consultantList = new ArrayList<>();
    public static List<Long> processedConsultantsIds = new ArrayList<>();
    public static ReentrantLock reentrantLock = new ReentrantLock();
    public static List<Appointment> appointmentList = new ArrayList<>();
    public static List<Long> processedAppointmentsIds = new ArrayList<>();

    @Override
    public void run() {
        while(true){
            try {
                getConsultantList();
                getAppointmentsList();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            CountDownLatch countDownLatch = new CountDownLatch(8);

            Thread thread1 = new Thread(new ProccessTimeRunnable(countDownLatch));
            Thread thread2 = new Thread(new ProccessTimeRunnable(countDownLatch));
            Thread thread3 = new Thread(new ProccessTimeRunnable(countDownLatch));
            Thread thread4 = new Thread(new ProccessTimeRunnable(countDownLatch));
            Thread thread5 = new Thread(new CheckAppointmentsRunnable(countDownLatch));
            Thread thread6 = new Thread(new CheckAppointmentsRunnable(countDownLatch));
            Thread thread7 = new Thread(new CheckAppointmentsRunnable(countDownLatch));
            Thread thread8 = new Thread(new CheckAppointmentsRunnable(countDownLatch));

            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            thread5.start();
            thread6.start();
            thread7.start();
            thread8.start();

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("List completed.");
            System.out.println(processedConsultantsIds.size());
            System.out.println(processedAppointmentsIds.size());
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
                    rs.getString("description"), rs.getBigDecimal("hourly_rate"), rs.getString("image_url"));
            consultantList.add(consultant);
        }
    }
    public static void getAppointmentsList() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments");

        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Appointment appointment = new Appointment(rs.getLong("id"), rs.getString("uuid"), rs.getString("title"), rs.getString("description"),
                    Categories.valueOf(rs.getString("category")), rs.getLong("user_id"), rs.getLong("consultant_id"),
                    LocalDateTime.parse(rs.getString("time_and_date"), formatter), rs.getBigDecimal("price"),
                    rs.getBoolean("is_accepted"), rs.getBoolean("is_paid"), UUID.fromString(rs.getString("room_uuid")));
            appointmentList.add(appointment);
        }
    }
}
