package com.OnlineConsultancyApp.runnables;

import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.services.AppointmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

import static com.OnlineConsultancyApp.runnables.CheckTimesLeftRunnable.*;
import static com.OnlineConsultancyApp.runnables.CheckTimesLeftRunnable.reentrantLock;

@Service
public class CheckAppointmentsRunnable implements Runnable{

    @Autowired
    AppointmentService appointmentService;

    @Override
    public void run() {
        for(Appointment appointment : appointmentList){
            reentrantLock.lock();
            if(processedAppointmentsIds.contains(appointment.getId())){
                reentrantLock.unlock();
            }else {
                try {
                    processedAppointmentsIds.add(appointment.getId());
                    reentrantLock.unlock();
                    LocalDateTime now = LocalDateTime.now();
                    long minutes = Duration.between(appointment.getTimeAndDate(), now).toMinutes();
                    if (minutes > 120) {
                        appointmentService.deleteAppointment(appointment.getId());
                    }
                    System.out.println(Thread.currentThread().getName() + " Appointment: " + appointment.getId());

                } catch (JsonProcessingException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
