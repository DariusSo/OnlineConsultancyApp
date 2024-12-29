package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.models.Consultant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.*;

@Service
public class RedisService {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisService() {
        String host = "localhost";
        int port = 6379;
        this.jedisPool = new JedisPool(host, port);
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



    public List<Consultant> get(String key) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return deserialize(data);
            }
            return null;
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

    public Set<String> getNewConsultantsKeys() throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("new_consultant.");
            return keys;
        }
    }

//    public void pipeline(int numberOfTickets) throws IOException {
//        try (Jedis jedis = jedisPool.getResource()) {
//            Pipeline p = jedis.pipelined();
//            for(int i = 0; i < numberOfTickets; i++){
//                Random random = new Random();
//                UUID uuid = UUID.randomUUID();
//                List<Integer> ticketNumbers = new ArrayList<>();
//                for(int j = 0; j < 5; j++){
//                    ticketNumbers.add(random.nextInt(1,35));
//                }
//                p.sadd(String.valueOf(uuid), String.valueOf(ticketNumbers));
//            }
//            //p.sync();
//        }
//    }

//    public void pipelineGet() throws IOException, ClassNotFoundException {
//        Lottery lottery = new Lottery();
//        List<Integer> luckyNumbers = lottery.generateLuckyNumbers();
//        List<Response> responses = new ArrayList<>();
//        try (Jedis jedis = jedisPool.getResource()) {
//            Pipeline p = jedis.pipelined();
//            Set<String> ticketKeys = getAllTicketsKeys();
//            for(String ticketKey : ticketKeys){
//                responses.add(p.get(ticketKey));
//
//                //int matchingNumbers = lottery.checkTicket(luckyNumbers ,deserialize(ticketPipeline.get().getBytes()));
//                //lottery.declareResult(matchingNumbers, ticketKey);
//            }
//            for(Response r : responses){
//                r.get();
//                int matchingNumbers = lottery.checkTicket(luckyNumbers , (List<Integer>) r.get());
//                lottery.declareResult(matchingNumbers, "test");
//            }
//        }
//    }

    private byte[] serialize(Object obj) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        byte[] json = mapper.writeValueAsString(obj).getBytes();
        return json;
    }
    private String serializeToString(Object obj) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    private List<Consultant> deserialize(byte[] data) throws IOException, ClassNotFoundException {

        ObjectMapper mapper = new ObjectMapper();
        List<Consultant> obj = mapper.readValue(data, List.class);
        return obj;
    }
    public void deleteDB(){
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
    }

    public void close() {
        jedisPool.close();
    }






}
