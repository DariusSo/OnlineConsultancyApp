package com.OnlineConsultancyApp;

import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.runnables.CheckTimesLeftRunnable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

import java.util.List;

@SpringBootApplication
@EnableCaching
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		Thread thread = new Thread(new CheckTimesLeftRunnable());
		thread.start();
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		ObjectMapper objectMapper = new ObjectMapper();
		GenericJackson2JsonRedisSerializer jacksonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig() //
				.prefixCacheNameWith(this.getClass().getPackageName() + ".") //
				.entryTtl(Duration.ofHours(1)) //
				.disableCachingNullValues()
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer));

		return RedisCacheManager.builder(connectionFactory) //
				.cacheDefaults(config) //
				.build();
	}

}
