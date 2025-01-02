package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Users.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIChatService {

    @Autowired
    AuthService authService;
    @Autowired
    RedisConversationService redisConversationService;

    private final ChatClient chatClient;

    ObjectMapper objectMapper;

    public AIChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }

    public String getAIResponse(String message, Categories consultantCategory) {
        // Process the message with the AI ChatClient and return the result
        return chatClient.prompt()
                .user(message).system("You are " + consultantCategory + " expert. Your job is to consult people about your expertise. You are not allow to talk about anything else.")
                .call()
                .content();
    }
    public String askAIConsultant(String token, String message, Categories consultantCategory) throws SQLException, IOException, ClassNotFoundException {
        User user = authService.getProfileInfo(token);
        List<Map<String, String>> conversation = getConversation(user.getId(), user.getRole(), consultantCategory);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", String.valueOf(Roles.USER).toLowerCase());
        userMessage.put("content", message);
        conversation.add(userMessage);
        String conversationString = serializeToString(conversation);

        String response = getAIResponse(conversationString, consultantCategory);
        conversation = deserializeConversation(conversationString);
        Map<String, String> assistantMessage = new HashMap<>();
        assistantMessage.put("role", String.valueOf(Roles.ASSISTANT).toLowerCase());
        assistantMessage.put("content", response);
        conversation.add(assistantMessage);
        if(conversation.size() > 8){
            conversation.remove(0);
            conversation.remove(0);
        }
        String conversationStringAgain = serializeToString(conversation);
        redisConversationService.putConversation(conversationStringAgain, user.getId(), user.getRole(), consultantCategory);
        return response;
    }
    public List<Map<String, String>> getConversation(long id, Roles role, Categories consultantCategory) throws IOException, ClassNotFoundException {
        String conversationString = redisConversationService.getConversation(id, role, consultantCategory);
        return deserializeConversation(conversationString);
    }
    public List<Map<String, String>> deserializeConversation(String conversationString) throws JsonProcessingException {
        if(conversationString == null){
            return new ArrayList<>();

        }else{
            return objectMapper.readValue(
                    conversationString,
                    new TypeReference<List<Map<String, String>>>() {}
            );
        }
    }
    private String serializeToString(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }
}
