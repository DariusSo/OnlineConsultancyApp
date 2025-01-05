package com.OnlineConsultancyApp.repositories;

import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.models.Messages.ForumMessage;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ForumRepository {

    public void createQuestion(ForumMessage forumMessage) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("INSERT INTO forum (name, question, answer, consultant_id) VALUES (?,?,?,?)");
        ps.setString(1, forumMessage.getName());
        ps.setString(2, forumMessage.getQuestion());
        ps.setString(3, forumMessage.getAnswer());
        ps.setLong(4, forumMessage.getConsultantId());
        ps.execute();
    }
    public void setAnswer(long id, String message) throws SQLException {
        PreparedStatement ps = Connect.SQLConnection("UPDATE forum SET answer = ? WHERE id = ?");
        ps.setString(1, message);
        ps.setLong(2, id);
        ps.execute();
    }
    public List<ForumMessage> getForumMessages(long consultantId) throws SQLException {
        List<ForumMessage> forumMessageList = new ArrayList<>();
        PreparedStatement ps = Connect.SQLConnection("SELECT * FROM forum WHERE consultant_id = ?");
        ps.setLong(1, consultantId);

        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            ForumMessage forumMessage = new ForumMessage(rs.getLong("id"), rs.getString("name"),
                                        rs.getString("question"), rs.getString("answer"),
                                        rs.getLong("consultant_id"));
            forumMessageList.add(forumMessage);
        }
        return forumMessageList;
    }

}
