package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.models.Messages.ForumMessage;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.OnlineConsultancyApp.Utilities.*;

@Repository
public class ForumRepository {

    public void createQuestion(ForumMessage forumMessage) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedDateTime = now.format(formatter);

        String sql = "INSERT INTO forum (name, question, answer, consultant_id, question_asked) VALUES (?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, forumMessage.getName());
            ps.setString(2, forumMessage.getQuestion());
            ps.setString(3, forumMessage.getAnswer());
            ps.setLong(4, forumMessage.getConsultantId());
            ps.setString(5, formattedDateTime);
            ps.execute();
        }
    }
    public void setAnswer(long id, String message) throws SQLException {
        String sql = "UPDATE forum SET answer = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, message);
            ps.setLong(2, id);
            ps.execute();
        }
    }
    public List<ForumMessage> getForumMessages(long consultantId) throws SQLException {
        List<ForumMessage> forumMessageList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String sql = "SELECT * FROM forum WHERE consultant_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, consultantId);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ForumMessage forumMessage = new ForumMessage(rs.getLong("id"), rs.getString("name"),
                        rs.getString("question"), rs.getString("answer"),
                        rs.getLong("consultant_id"), LocalDateTime.parse(rs.getString("question_asked"), formatter));
                forumMessageList.add(forumMessage);
            }
            return forumMessageList;
        }
    }

    public ForumMessage getMessageById(long id) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String sql = "SELECT * FROM forum WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ForumMessage forumMessage = new ForumMessage(rs.getLong("id"), rs.getString("name"),
                        rs.getString("question"), rs.getString("answer"),
                        rs.getLong("consultant_id"), LocalDateTime.parse(rs.getString("question_asked"), formatter));
                return forumMessage;
            }
            return new ForumMessage();
        }
    }

}
