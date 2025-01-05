package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.exceptions.NoAccessException;
import com.OnlineConsultancyApp.models.Messages.ForumMessage;
import com.OnlineConsultancyApp.repositories.ForumRepository;
import com.OnlineConsultancyApp.security.JwtDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ForumService {

    @Autowired
    ForumRepository forumRepository;

    public void createQuestion(ForumMessage forumMessage) throws SQLException {
        forumRepository.createQuestion(forumMessage);
    }

    public void setAnswer(String token, long id, String message) throws SQLException {
        try{
            JwtDecoder.decodedUserId(token);
        } catch (Exception e){
            throw new NoAccessException();
        }
        forumRepository.setAnswer(id, message);
    }

    public List<ForumMessage> getForumMessages(long consultantId) throws SQLException {
        return forumRepository.getForumMessages(consultantId);
    }

}
