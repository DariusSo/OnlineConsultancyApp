package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.Exceptions.BadEmailOrPasswordException;
import com.OnlineConsultancyApp.Exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Client;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.models.User;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConsultantRepository {

    public void registerConsultant(Consultant consultant) throws SQLException {

        PreparedStatement ps = Connect.SQLConnection("INSERT INTO consultants (first_name, last_name, email, password, role, appointments_ids," +
                "categories, available_time, speciality, description, hourly_rate, phone)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, consultant.getFirstName());
        ps.setString(2, consultant.getLastName());
        ps.setString(3, consultant.getEmail());
        ps.setString(4, consultant.getPassword());
        ps.setString(5, String.valueOf(consultant.getRole()));
        ps.setString(6, consultant.getAppointmentsId());
        ps.setString(7, consultant.getCategories());
        ps.setString(8, consultant.getAvailableTime());
        ps.setString(9, consultant.getSpeciality());
        ps.setString(10, consultant.getDescription());
        ps.setBigDecimal(11, consultant.getHourlyRate());
        ps.setString(12, consultant.getPhone());
        ps.execute();

    }

    public User getConsultantByEmail(String email) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM consultants WHERE email = ?");

        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            User consultant = new Consultant(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("email"), rs.getString("phone"), rs.getString("appointments_ids"), Roles.valueOf(rs.getString("role")),
                    rs.getString("categories"), rs.getString("available_time"), rs.getString("speciality"),
                    rs.getString("description"), rs.getBigDecimal("hourly_rate"));
            return consultant;
        }else{
            throw new NoSuchUserException();
        }
    }

    public Consultant getConsultantById(long id) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM consultants WHERE id = ?");

        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            Consultant consultant = new Consultant(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("email"), rs.getString("phone"), rs.getString("appointments_ids"), Roles.valueOf(rs.getString("role")),
                    rs.getString("categories"), rs.getString("available_time"), rs.getString("speciality"),
                    rs.getString("description"), rs.getBigDecimal("hourly_rate"));
            return consultant;
        }else{
            throw new NoSuchUserException();
        }
    }

    public List<Consultant> getNewestConsultants() throws SQLException {

        List<Consultant> consultantList = new ArrayList<>();
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM consultants ORDER BY id DESC LIMIT 10");
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            Consultant consultant = new Consultant(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("email"), rs.getString("phone"), rs.getString("appointments_ids"), Roles.valueOf(rs.getString("role")),
                    rs.getString("categories"), rs.getString("available_time"), rs.getString("speciality"),
                    rs.getString("description"), rs.getBigDecimal("hourly_rate"));
            consultantList.add(consultant);
        }
        return consultantList;
    }


    public User getAuthUser(String email) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM consultants WHERE email = ?");

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

    public void updateDates(String availableTime, long id) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("UPDATE consultants SET available_time = ? WHERE id = ?");
        ps.setString(1, availableTime);
        ps.setLong(2, id);
        ps.execute();
    }

}
