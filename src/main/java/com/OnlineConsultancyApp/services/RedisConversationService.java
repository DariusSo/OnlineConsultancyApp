package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RedisConversationService {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisConversationService() {
        String host = "localhost";
        int port = 6379;
        this.jedisPool = new JedisPool(host, port);
    }

    public void putConversation(String conversationString, long userId, Roles role) throws IOException, ClassNotFoundException {
        String prefix = getPrefix(role);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(prefix + userId, conversationString);
        }
    }
    public String getConversation(long userId, Roles role) throws IOException, ClassNotFoundException {
        String prefix = getPrefix(role);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(prefix + userId);
        }
    }
    public String getPrefix(Roles role){
        if(role == Roles.CLIENT){
            return "client-";
        } else if (role == Roles.CONSULTANT) {
            return "consultant-";
        }else{
            throw new ThereIsNoSuchRoleException();
        }
    }
}
