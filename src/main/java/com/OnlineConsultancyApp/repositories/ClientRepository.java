package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.Exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.Exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Repository
public class ClientRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void registerClient(Client client) throws SQLException {

        PreparedStatement ps = Connect.SQLConnection("INSERT INTO clients (first_name, last_name, email, password, role, birth_date, phone)" +
                "VALUES (?,?,?,?,?,?,?)");
        ps.setString(1, client.getFirstName());
        ps.setString(2, client.getLastName());
        ps.setString(3, client.getEmail());
        ps.setString(4, client.getPassword());
        ps.setString(5, String.valueOf(client.getRole()));
        ps.setString(6, String.valueOf(client.getBirthDate()));
        ps.setString(7, client.getPhone());
        ps.execute();

    }

    public Client getClientById(long id) throws SQLException, JsonProcessingException {
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM clients WHERE id = ?");

        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            List<Long> appointmentList = objectMapper.convertValue(rs.getString("appointments_ids"), new TypeReference<List<Long>>() {
            });
            Client client = new Client(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("email"), rs.getString("phone"), appointmentList, Roles.valueOf(rs.getString("role")),
                    LocalDate.parse(rs.getString("birth_date")));
            return client;
        }else{
            throw new NoSuchUserException();
        }
    }

    public User getClientByEmail(String email) throws SQLException, JsonProcessingException {
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM clients WHERE email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            List<Long> appointmentList = objectMapper.readValue(rs.getString("appointments_ids"), List.class);
            User client = new Client(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("email"), rs.getString("phone"), appointmentList, Roles.valueOf(rs.getString("role")),
                    LocalDate.parse(rs.getString("birth_date")));
            return client;
        }else{
            throw new NoSuchUserException();
        }
    }

    public User getAuthUser(String email) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM clients WHERE email = ?");

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

    public void addAppointment(long id, long appointmentId) throws SQLException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Client client = getClientById(id);
        List<Long> appointmentList = client.getAppointmentsId();
        if(appointmentList == null){
            appointmentList = new ArrayList<>();
        }
        appointmentList.add(appointmentId);

        String appointmentsIdString = objectMapper.writeValueAsString(appointmentList);

        PreparedStatement ps = Connect.SQLConnection("UPDATE clients SET appointments_ids = ? WHERE id = ?");
        ps.setString(1, appointmentsIdString);
        ps.setLong(2, id);
    }
}
