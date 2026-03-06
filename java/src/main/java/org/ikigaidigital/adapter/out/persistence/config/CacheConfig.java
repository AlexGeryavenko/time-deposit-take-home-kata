package org.ikigaidigital.adapter.out.persistence.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager(
      @Value("${api.cache.max-size:200}") int maxSize,
      @Value("${api.cache.ttl-minutes:10}") int ttlMinutes) {
    CaffeineCacheManager manager = new CaffeineCacheManager("deposits", "withdrawals");
    manager.setCaffeine(Caffeine.newBuilder()
        .maximumSize(maxSize)
        .expireAfterWrite(Duration.ofMinutes(ttlMinutes)));
    return manager;
  }
}
