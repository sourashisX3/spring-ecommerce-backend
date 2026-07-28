package com.example.ecommerce_backend.core.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final RateLimitConfig rateLimitConfig;
    private final Cache<String, Bucket> bucketCache;

    public RateLimitService(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
        this.bucketCache = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();
    }

    public boolean tryConsume(String key, String group) {
        Bucket bucket = bucketCache.get(key + ":" + group, k -> createNewBucket(group));
        return bucket.tryConsume(1);
    }

    private Bucket createNewBucket(String group) {
        return Bucket.builder()
                .addLimit(rateLimitConfig.resolveBandwidth(group))
                .build();
    }
}
