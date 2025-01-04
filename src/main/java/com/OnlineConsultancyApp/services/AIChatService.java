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
        // Process the message with the AI and return the result
        return chatClient.prompt()
                .user(message).system("You are " + consultantCategory + " expert. Your job is to consult people about your expertise. " +
                        "                   You are not allow to talk about anything else.")
                .call()
                .content();
    }
    public String askAIConsultant(String token, String message, Categories consultantCategory) throws SQLException, IOException, ClassNotFoundException {
        //Validate and get info
        User user = authService.getProfileInfo(token);

        //Get conversation from redis
        List<Map<String, String>> conversation = getConversation(user.getId(), user.getRole(), consultantCategory);

        //Add user message to conversation
        conversation = updateConversation(conversation, message, Roles.USER);

        //Serialize and send message to chatGPT
        String conversationString = serializeToString(conversation);
        String response = getAIResponse(conversationString, consultantCategory);

        //Update conversation with AI response
        conversation = deserializeConversation(conversationString);
        conversation = updateConversation(conversation, response, Roles.ASSISTANT);

        //Keep limited conversations because of money
        conversation = trimConversationMaybe(conversation);

        //Put updated conversation to redis
        String conversationStringAgain = serializeToString(conversation);
        redisConversationService.putConversation(conversationStringAgain, user.getId(), user.getRole(), consultantCategory);

        return response;
    }
    public List<Map<String, String>> updateConversation(List<Map<String, String>> conversation, String message, Roles role){
        Map<String, String> messageToAdd = new HashMap<>();
        messageToAdd.put("role", String.valueOf(role).toLowerCase());
        messageToAdd.put("content", message);
        conversation.add(messageToAdd);
        return conversation;
    }

    public List<Map<String, String>> trimConversationMaybe(List<Map<String, String>> conversation){
        if(conversation.size() > 8){
            conversation.remove(0);
            conversation.remove(0);
        }
        return conversation;
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
