package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.Utilities;
import com.OnlineConsultancyApp.exceptions.NoSuchAppointmentException;
import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.models.Appointment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.OnlineConsultancyApp.Utilities.*;

@Repository
public class AppointmentRepository {

    public void addAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (title, description, category, user_id, consultant_id, time_and_date, price, " +
                "is_accepted, uuid, is_paid, room_uuid) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
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
            ps.setString(11, String.valueOf(appointment.getRoomUuid()));
            ps.execute();
        }
    }

    public List<Appointment> getAppointmentsByUserId(long id) throws SQLException {
        List<Appointment> appointmentList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String sql = "SELECT * FROM appointments WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getLong("id"),
                        rs.getString("uuid"),
                        rs.getString("title"),
                        rs.getString("description"),
                        Categories.valueOf(rs.getString("category")),
                        rs.getLong("user_id"),
                        rs.getLong("consultant_id"),
                        LocalDateTime.parse(rs.getString("time_and_date"), formatter),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("is_accepted"),
                        rs.getBoolean("is_paid"),
                        UUID.fromString(rs.getString("room_uuid")));
                appointmentList.add(appointment);
            }
        }
        return appointmentList;

    }

    public Appointment getAppointmentsRoomUuid(UUID uuid) throws SQLException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String sql = "SELECT * FROM appointments WHERE room_uuid = ?";

        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(uuid));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Appointment(
                        rs.getLong("id"),
                        rs.getString("uuid"),
                        rs.getString("title"),
                        rs.getString("description"),
                        Categories.valueOf(rs.getString("category")),
                        rs.getLong("user_id"),
                        rs.getLong("consultant_id"),
                        LocalDateTime.parse(rs.getString("time_and_date"), formatter),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("is_accepted"),
                        rs.getBoolean("is_paid"),
                        UUID.fromString(rs.getString("room_uuid")));
            } else {
                throw new NoSuchAppointmentException();
            }
        }
    }

    public List<Appointment> getAppointmentsByConsultantId(long id) throws SQLException {
        List<Appointment> appointmentList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String sql = "SELECT * FROM appointments WHERE consultant_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getLong("id"),
                        rs.getString("uuid"),
                        rs.getString("title"),
                        rs.getString("description"),
                        Categories.valueOf(rs.getString("category")),
                        rs.getLong("user_id"),
                        rs.getLong("consultant_id"),
                        LocalDateTime.parse(rs.getString("time_and_date"), formatter),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("is_accepted"),
                        rs.getBoolean("is_paid"),
                        UUID.fromString(rs.getString("room_uuid")));
                appointmentList.add(appointment);
            }
            return appointmentList;
        }
    }

    public Appointment getAppointmentsByAppointmenttId(long id) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Appointment(
                        rs.getLong("id"),
                        rs.getString("uuid"),
                        rs.getString("title"),
                        rs.getString("description"),
                        Categories.valueOf(rs.getString("category")),
                        rs.getLong("user_id"),
                        rs.getLong("consultant_id"),
                        LocalDateTime.parse(rs.getString("time_and_date"), formatter),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("is_accepted"),
                        rs.getBoolean("is_paid"),
                        UUID.fromString(rs.getString("room_uuid")));
            } else {
                throw new NoSuchAppointmentException();
            }
        }
    }

    public long getAppointmentId(long userId, long consultantId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE user_id = ? AND consultant_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, consultantId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            } else {
                throw new NoSuchAppointmentException();
            }
        }
    }

    public void updatePaidStatus(UUID uuid) throws SQLException {
        String sql = "UPDATE appointments SET is_paid = ? WHERE uuid = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setString(2, String.valueOf(uuid));
            ps.execute();
        }
    }

    public void confirmAppointment(long id) throws SQLException {
        String sql = "UPDATE appointments SET is_accepted = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setLong(2, id);
            ps.execute();
        }
    }

    public void deleteAppointment(long id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.execute();
        }
    }

    public void addStripeSessionId(String sessionId, UUID uuid) throws SQLException {
        String sql = "UPDATE appointments SET stripe_session_id = ? WHERE uuid = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setString(2, String.valueOf(uuid));
            ps.execute();
        }
    }

    public String getStripeSessionId(long id) throws SQLException {
        String sql = "SELECT stripe_session_id FROM appointments WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("stripe_session_id");
            }
            return "";
        }
    }

}
