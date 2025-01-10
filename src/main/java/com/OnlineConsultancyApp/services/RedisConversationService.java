package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.exceptions.ThereIsNoSuchRoleException;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RedisConversationService {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisConversationService(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port,
            @Value("${spring.redis.password}") String password
    ) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
    }
    //Store conversation with 1 hour expiration
    public void putConversation(String conversationString, long userId, Roles role, Categories consultantCategory) throws IOException, ClassNotFoundException {
        String prefix = getPrefix(role, consultantCategory);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(prefix + userId, 3600, conversationString);
        }
    }
    public String getConversation(long userId, Roles role, Categories consultantCategory) throws IOException, ClassNotFoundException {
        String prefix = getPrefix(role, consultantCategory);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(prefix + userId);
        }
    }
    public String getPrefix(Roles role, Categories consultantCategory){
        if(role == Roles.CLIENT){
            return consultantCategory + "-client-";
        } else if (role == Roles.CONSULTANT) {
            return consultantCategory + "-consultant-";
        }else{
            throw new ThereIsNoSuchRoleException();
        }
    }
}
