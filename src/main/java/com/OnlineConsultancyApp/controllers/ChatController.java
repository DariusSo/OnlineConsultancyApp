package com.OnlineConsultancyApp.controllers;

import com.OnlineConsultancyApp.models.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/consultation/{sessionId}")
    @SendTo("/topic/consultation/{sessionId}")
    public ChatMessage echoChatMessage(ChatMessage chatMessage){
        return chatMessage;
    }
}
