package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.models.Users.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClientRepository {

    @Value("${db.url}")
    private String URL;

    @Value("${db.username}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void registerClient(Client client) throws SQLException {
        String sql = "INSERT INTO clients (first_name, last_name, email, password, role, birth_date, phone) VALUES (?,?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getPassword());
            ps.setString(5, String.valueOf(client.getRole()));
            ps.setString(6, String.valueOf(client.getBirthDate()));
            ps.setString(7, client.getPhone());
            ps.execute();
        }
    }

    public void editClient(Client client, long id) throws SQLException {
        String sql = "UPDATE clients SET first_name = ?, last_name = ?, email = ?, phone = ?, image_url = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getPhone());
            ps.setString(5, client.getImageUrl());
            ps.setLong(6, id);
            ps.execute();
        }
    }

    public Client getClientById(long id) throws SQLException, JsonProcessingException {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                return new Client(rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        LocalDate.parse(rs.getString("birth_date")),
                        rs.getString("image_url"));
            }else{
                throw new NoSuchUserException();
            }
        }
    }

    public User getClientByEmail(String email) throws SQLException, JsonProcessingException {
        String sql = "SELECT * FROM clients WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {});
                return new Client(rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        appointmentList,
                        Roles.valueOf(rs.getString("role")),
                        LocalDate.parse(rs.getString("birth_date")),
                        rs.getString("image_url"));
            }else{
                throw new NoSuchUserException();
            }
        }
    }

    public User getAuthUser(String email) throws SQLException {
        String sql = "SELECT * FROM clients WHERE email = ?";
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
}
