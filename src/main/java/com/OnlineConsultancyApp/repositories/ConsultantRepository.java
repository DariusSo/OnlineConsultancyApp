package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.Utilities;
import com.OnlineConsultancyApp.exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.models.Users.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultantRepository {

    @Value("${db.url}")
    private String URL;

    @Value("${db.username}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public long registerConsultant(Consultant consultant) throws SQLException, JsonProcessingException {
        System.out.println(URL);
        String sql = "INSERT INTO consultants (first_name, last_name, email, password, role, categories, available_time, " +
                     "speciality, description, hourly_rate, phone) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, consultant.getFirstName());
            ps.setString(2, consultant.getLastName());
            ps.setString(3, consultant.getEmail());
            ps.setString(4, consultant.getPassword());
            ps.setString(5, String.valueOf(consultant.getRole()));
            ps.setString(6, consultant.getCategories());
            ps.setString(7, consultant.getAvailableTime());
            ps.setString(8, consultant.getSpeciality());
            ps.setString(9, consultant.getDescription());
            ps.setBigDecimal(10, consultant.getHourlyRate());
            ps.setString(11, consultant.getPhone());
            ps.execute();

            return getConsultant(consultant.getEmail()).getId();
        }
    }
    public void editConsultant(Consultant consultant, long id) throws SQLException {
        String sql = "UPDATE consultants SET first_name = ?, last_name = ?, email = ?, speciality = ?, description = ?, " +
                     "hourly_rate = ?, phone = ?, image_url = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, consultant.getFirstName());
            ps.setString(2, consultant.getLastName());
            ps.setString(3, consultant.getEmail());
            ps.setString(4, consultant.getSpeciality());
            ps.setString(5, consultant.getDescription());
            ps.setBigDecimal(6, consultant.getHourlyRate());
            ps.setString(7, consultant.getPhone());
            ps.setString(8, consultant.getImageUrl());
            ps.setLong(9, id);
            ps.execute();
        }
    }

    public User getConsultant(String email) throws SQLException, JsonProcessingException {
        String sql = "SELECT * FROM consultants WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                return new Consultant(rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        rs.getString("categories"),
                        rs.getString("available_time"),
                        rs.getString("speciality"),
                        rs.getString("description"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("image_url"));
            }else{
                throw new NoSuchUserException();
            }
        }
    }

    public Consultant getConsultant(long id) throws SQLException, JsonProcessingException {
        String sql = "SELECT * FROM consultants WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                return new Consultant(rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        rs.getString("categories"),
                        rs.getString("available_time"),
                        rs.getString("speciality"),
                        rs.getString("description"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("image_url"));
            }else{
                throw new NoSuchUserException();
            }
        }
    }
    public List<Consultant> getConsultantsByCategory(Categories category) throws SQLException {
        List<Consultant> consultantList = new ArrayList<>();
        String sql = "SELECT * FROM consultants WHERE categories = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(category));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                Consultant consultant = new Consultant(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        rs.getString("categories"),
                        rs.getString("available_time"),
                        rs.getString("speciality"),
                        rs.getString("description"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("image_url"));
                consultantList.add(consultant);
            }
            return consultantList;
        }
    }

    public List<Consultant> getNewestConsultants() throws SQLException, JsonProcessingException {
        List<Consultant> consultantList = new ArrayList<>();
        String sql = "SELECT * FROM consultants ORDER BY id DESC LIMIT 10";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                Consultant consultant = new Consultant(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        rs.getString("categories"),
                        rs.getString("available_time"),
                        rs.getString("speciality"),
                        rs.getString("description"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("image_url"));
                consultantList.add(consultant);
            }
            return consultantList;
        }
    }

    public User getAuthUser(String email) throws SQLException {
        String sql = "SELECT * FROM consultants WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                User client = new Client();
                client.setId(rs.getLong("id"));
                client.setEmail(rs.getString("email"));
                client.setPassword(rs.getString("password"));
                return client;
            }else{
                throw new BadEmailOrPasswordException();
            }
        }
    }

    public void updateDates(String availableTime, long id) throws SQLException {
        String sql = "UPDATE consultants SET available_time = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, availableTime);
            ps.setLong(2, id);
            ps.execute();
        }
    }

    public String getDates(long id) throws SQLException {
        String sql = "SELECT * FROM consultants WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("available_time");
            }else{
                throw new NoSuchUserException();
            }
        }
    }

    public List<Consultant> getConsultantsWithoutCategory(double minPrice, double maxPrice, String speciality) throws SQLException {
        List<Consultant> consultantList = new ArrayList<>();
        String sql = "SELECT * FROM consultants WHERE hourly_rate > ? AND hourly_rate < ? AND speciality LIKE ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ps.setString(3, "%" + speciality + "%");

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                Consultant consultant = new Consultant(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        rs.getString("categories"),
                        rs.getString("available_time"),
                        rs.getString("speciality"),
                        rs.getString("description"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("image_url"));
                consultantList.add(consultant);
            }
            return consultantList;
        }
    }

    public List<Consultant> getConsultantsWithCategory(double minPrice, double maxPrice, String speciality, Categories category) throws SQLException {
        List<Consultant> consultantList = new ArrayList<>();
        String sql = "SELECT * FROM consultants WHERE hourly_rate > ? AND hourly_rate < ? AND speciality LIKE ? AND categories = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ps.setString(3, "%" + speciality + "%");
            ps.setString(4, String.valueOf(category));

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                Consultant consultant = new Consultant(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        rs.getString("categories"),
                        rs.getString("available_time"),
                        rs.getString("speciality"),
                        rs.getString("description"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("image_url"));
                consultantList.add(consultant);
            }
            return consultantList;
        }
    }
    public List<Consultant> getConsultantList() throws SQLException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Consultant> consultantList = new ArrayList<>();
        String sql = "SELECT * FROM consultants";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                Consultant consultant = new Consultant(rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        rs.getString("categories"),
                        rs.getString("available_time"),
                        rs.getString("speciality"),
                        rs.getString("description"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getString("image_url"));
                consultantList.add(consultant);
            }
        }
        return consultantList;
    }

}
