package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.Exceptions.NoSuchAppointmentException;
import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.models.Appointment;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class AppointmentRepository {

    public void addAppointment(Appointment appointment) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(String.valueOf(appointment.getTimeAndDate()), formatter);
        PreparedStatement ps = Connect.SQLConnection("INSERT INTO appointments (title, description, category, " +
                                                            "user_id, consultant_id, time_and_date, price, is_accepted) VALUES " +
                                                            "(?,?,?,?,?,?,?,?)");
        ps.setString(1, appointment.getTitle());
        ps.setString(2, appointment.getDescription());
        ps.setString(3, String.valueOf(appointment.getCategory()));
        ps.setLong(4, appointment.getUserId());
        ps.setLong(5, appointment.getConsultantId());
        ps.setString(6, String.valueOf(appointment.getTimeAndDate()));
        ps.setBigDecimal(7, appointment.getPrice());
        ps.setBoolean(8, false);
        ps.execute();
    }

    public Appointment getAppointmentsByUserId(long id) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments WHERE user_id = ?");
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Appointment appointment = new Appointment(rs.getLong("id"), rs.getString("title"), rs.getString("description"),
                    Categories.valueOf(rs.getString("category")), rs.getLong("user_id"), rs.getLong("consultant_id"),
                            LocalDateTime.parse(rs.getString("time_and_date"), formatter), rs.getBigDecimal("price"), rs.getBoolean("is_accepted"));
            return appointment;
        }else{
            throw new NoSuchAppointmentException();
        }

    }

    public Appointment getAppointmentsByConsultantId(long id) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments WHERE consultant_id = ?");
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Appointment appointment = new Appointment(rs.getLong("id"), rs.getString("title"), rs.getString("description"),
                    Categories.valueOf(rs.getString("category")), rs.getLong("user_id"), rs.getLong("consultant_id"),
                    LocalDateTime.parse(rs.getString("time_and_date"), formatter), rs.getBigDecimal("price"), rs.getBoolean("is_accepted"));
            return appointment;
        }else{
            throw new NoSuchAppointmentException();
        }
    }

    public Appointment getAppointmentsByAppointmenttId(long id) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM appointments WHERE id = ?");
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Appointment appointment = new Appointment(rs.getLong("id"), rs.getString("title"), rs.getString("description"),
                    Categories.valueOf(rs.getString("category")), rs.getLong("user_id"), rs.getLong("consultant_id"),
                    LocalDateTime.parse(rs.getString("time_and_date"), formatter), rs.getBigDecimal("price"), rs.getBoolean("is_accepted"));
            return appointment;
        }else{
            throw new NoSuchAppointmentException();
        }
    }

    public void confirmAppointment(long id) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("UPDATE appointments SET is_accepted = ? WHERE id = ?");
        ps.setBoolean(1, true);
        ps.setLong(2, id);
        ps.execute();
    }
    public void deleteAppointment(long id) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("DELETE * FROM clients WHERE id = ?");
        ps.setLong(1, id);
        ps.execute();
    }

}
