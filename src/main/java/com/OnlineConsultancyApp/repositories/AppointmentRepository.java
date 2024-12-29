package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.exceptions.NoSuchAppointmentException;
import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.models.Appointment;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AppointmentRepository {

    public void addAppointment(Appointment appointment) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("INSERT INTO appointments (title, description, category, " +
                                                            "user_id, consultant_id, time_and_date, price, is_accepted, uuid, is_paid) VALUES " +
                                                            "(?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, appointment.getTitle());
        ps.setString(2, appointment.getDescription());
        ps.setString(3, String.valueOf(appointment.getCategory()));
        ps.setLong(4, appointment.getUserId());
        ps.setLong(5, appointment.getConsultantId());
        ps.setString(6, String.valueOf(appointment.getTimeAndDate()));
        ps.setBigDecimal(7, appointment.getPrice());
        ps.setBoolean(8, false);
        ps.setString(9, appointment.getUuid());
        ps.setBoolean(10, false);
        ps.execute();


    }

    public List<Appointment> getAppointmentsByUserId(long id) throws SQLException {
        List<Appointment> appointmentList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments WHERE user_id = ?");
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Appointment appointment = new Appointment(rs.getLong("id"), rs.getString("uuid"), rs.getString("title"), rs.getString("description"),
                    Categories.valueOf(rs.getString("category")), rs.getLong("user_id"), rs.getLong("consultant_id"),
                            LocalDateTime.parse(rs.getString("time_and_date"), formatter), rs.getBigDecimal("price"), rs.getBoolean("is_accepted"), rs.getBoolean("is_paid"));
            appointmentList.add(appointment);
        }else{
            throw new NoSuchAppointmentException();
        }
        return appointmentList;

    }

    public List<Appointment> getAppointmentsByConsultantId(long id) throws SQLException {
        List<Appointment> appointmentList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments WHERE consultant_id = ?");
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Appointment appointment = new Appointment(rs.getLong("id"), rs.getString("uuid"), rs.getString("title"), rs.getString("description"),
                    Categories.valueOf(rs.getString("category")), rs.getLong("user_id"), rs.getLong("consultant_id"),
                    LocalDateTime.parse(rs.getString("time_and_date"), formatter), rs.getBigDecimal("price"), rs.getBoolean("is_accepted"), rs.getBoolean("is_paid"));
            appointmentList.add(appointment);
        }
        return appointmentList;
    }

    public Appointment getAppointmentsByAppointmenttId(long id) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments WHERE id = ?");
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Appointment appointment = new Appointment(rs.getLong("id"), rs.getString("uuid"), rs.getString("title"), rs.getString("description"),
                    Categories.valueOf(rs.getString("category")), rs.getLong("user_id"), rs.getLong("consultant_id"),
                    LocalDateTime.parse(rs.getString("time_and_date"), formatter), rs.getBigDecimal("price"), rs.getBoolean("is_accepted"), rs.getBoolean("is_paid"));
            return appointment;
        }else{
            throw new NoSuchAppointmentException();
        }
    }

    public long getAppointmentId(long userId, long consultantId) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments WHERE user_id = ? AND consultant_id = ?");
        ps.setLong(1, userId);
        ps.setLong(2, consultantId);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getLong("id");
        }else{
            throw new NoSuchAppointmentException();
        }
    }

    public void updatePaidStatus(UUID uuid) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("UPDATE appointments SET is_paid = ? WHERE uuid = ?");
        ps.setBoolean(1, true);
        ps.setString(2, String.valueOf(uuid));
        ps.execute();
    }

    public void confirmAppointment(long id) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("UPDATE appointments SET is_accepted = ? WHERE id = ?");
        ps.setBoolean(1, true);
        ps.setLong(2, id);
        ps.execute();
    }
    public void deleteAppointment(long id) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("DELETE FROM appointments WHERE id = ?");
        ps.setLong(1, id);
        ps.execute();
    }

}
