package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.models.Users.Consultant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.*;

@Service
public class RedisCacheService {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisCacheService(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port,
            @Value("${spring.redis.password}") String password
    ) {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
    }

    public void put(Consultant consultant) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            List<Consultant> consultantList = getNewConsultants();
            if(consultantList != null && consultantList.size() > 9){
                jedis.rpop("newConsultants");
            }
            jedis.lpush("newConsultants", serializeToString(consultant));
        }
    }
    public void editConsultantInCache(Consultant consultant, long id) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            String consultantString = jedis.lindex("newConsultants", id);
            jedis.lpush("newConsultants", serializeToString(consultant));
            jedis.lrem("newConsultants", id, consultantString);
        }
    }

    public List<Consultant> getNewConsultants() throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> newConsultantsList = jedis.lrange("newConsultants", 0, -1);
            if (!newConsultantsList.isEmpty()) {
                return desirializeConsultantList(newConsultantsList);
            }
            return null;
        }
    }

    public List<Consultant> desirializeConsultantList(List<String> consultantsStrings) throws JsonProcessingException {
        List<Consultant> cList = new ArrayList<>();
        for(String s : consultantsStrings){
            Consultant consultant = objectMapper.readValue(s, Consultant.class);
            cList.add(consultant);
        }
        return cList;
    }
    private String serializeToString(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }

    public void deleteDB(){
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
    }

    public void close() {
        jedisPool.close();
    }






}
