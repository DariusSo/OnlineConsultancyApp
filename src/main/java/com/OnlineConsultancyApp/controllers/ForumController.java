package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.exceptions.NoAccessException;
import com.OnlineConsultancyApp.models.Messages.ForumMessage;
import com.OnlineConsultancyApp.services.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    ForumService forumService;

    @PostMapping
    public ResponseEntity<String> createQuestion(ForumMessage forumMessage){
        try{
            forumService.createQuestion(forumMessage);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database problems", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping
    public ResponseEntity<String> setAnswer(@RequestHeader("Authorization") String token, long messageId, String message){
        try{
            forumService.setAnswer(token, messageId, message);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database problems", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoAccessException e){
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping
    public ResponseEntity<List<ForumMessage>> getForumMessages(long consultantId){
        try{
            List<ForumMessage> forumMessageList = forumService.getForumMessages(consultantId);
            return new ResponseEntity<>(forumMessageList, HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
