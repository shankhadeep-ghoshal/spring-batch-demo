package com.shankhadeepghoshal.springbatch;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Configuration
public class UtilsConfig {

  @Bean
  public ObjectMapper objectMapper() {
    final var objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return objectMapper;
  }

  @Bean
  public Supplier<ThreadLocalRandom> getRandom() {
    return ThreadLocalRandom::current;
  }

  @Bean
  public RedisClient redisClient(@Value("${redis.uri}") final String redisUriString) {
    return RedisClient.create(redisUriString);
  }

  @Bean
  public GenericObjectPool<StatefulRedisConnection<String, String>> objectPool(
      final RedisClient client) {
    return ConnectionPoolSupport.createGenericObjectPool(
        client::connect, new GenericObjectPoolConfig<>());
  }
}
