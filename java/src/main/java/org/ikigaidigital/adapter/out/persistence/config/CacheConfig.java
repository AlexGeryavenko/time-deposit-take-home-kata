package org.ikigaidigital.adapter.out.persistence.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.cache.caffeine.max-size:100}")
    private int maxSize;

    @Value("${spring.cache.caffeine.ttl-seconds:300}")
    private long ttlSeconds;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("deposits", "withdrawals");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS));
        return cacheManager;
    }
}
