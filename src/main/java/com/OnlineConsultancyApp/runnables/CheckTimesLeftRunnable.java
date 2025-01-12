package com.OnlineConsultancyApp.runnables;

import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.services.AppointmentService;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    ConsultantService consultantService;
    @Autowired
    AppointmentService appointmentService;
    @Autowired
    ProccessTimeRunnable proccessTimeRunnable;
    @Autowired
    CheckAppointmentsRunnable checkAppointmentsRunnable;

    public static List<Consultant> consultantList = new ArrayList<>();
    public static List<Long> processedConsultantsIds = new ArrayList<>();
    public static ReentrantLock reentrantLock = new ReentrantLock();
    public static List<Appointment> appointmentList = new ArrayList<>();
    public static List<Long> processedAppointmentsIds = new ArrayList<>();

    @Override
    public void run() {
        while(true){
            try {
                consultantList = consultantService.getConsultantList();
                appointmentList = appointmentService.getAppointmentList();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Thread thread1 = new Thread(proccessTimeRunnable);
            Thread thread2 = new Thread(proccessTimeRunnable);
            Thread thread3 = new Thread(proccessTimeRunnable);
            Thread thread4 = new Thread(proccessTimeRunnable);
            Thread thread5 = new Thread(checkAppointmentsRunnable);
            Thread thread6 = new Thread(checkAppointmentsRunnable);
            Thread thread7 = new Thread(checkAppointmentsRunnable);
            Thread thread8 = new Thread(checkAppointmentsRunnable);

            thread5.start();
            thread6.start();
            thread7.start();
            thread8.start();
            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();

            try {
                Thread.sleep(3600 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processedConsultantsIds = new ArrayList<>();
            consultantList = new ArrayList<>();
            processedAppointmentsIds = new ArrayList<>();
            appointmentList = new ArrayList<>();
        }
    }
}
