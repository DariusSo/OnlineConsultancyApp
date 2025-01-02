package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.services.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
public class AIChatController {

    @Autowired
    AIChatService aiChatService;

    @GetMapping("/ai")
    public String askAIConsultant(@RequestHeader("Authorization") String token, String message, Categories consultantCategory) throws SQLException, IOException, ClassNotFoundException {
        return aiChatService.askAIConsultant(token, message, consultantCategory);
    }
}
