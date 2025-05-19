package com.zicket.zicket.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zicket.zicket.entity.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void set(String key, Object o, Long ttl)
    {
        try
        {
            ObjectMapper mapper=new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json=mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            log.error("Error while setting in redis", e);
        }
    }

    public List<Ticket> get(String key)
    {
        try
        {
            ObjectMapper mapper=new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = (String) redisTemplate.opsForValue().get(key);
            if(json==null)
            {
                return null;
            }
            return mapper.readValue(json, new TypeReference<List<Ticket>>() {});
        }
        catch (Exception e)
        {
            log.error("Error while getting in redis", e);
            return null;
        }
    }

}
