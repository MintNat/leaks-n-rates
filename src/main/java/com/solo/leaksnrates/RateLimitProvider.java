package com.solo.leaksnrates;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log
@Component
class RateLimitProvider {
    @Value("${ratelimit.requestsnumber}")
    private int REQUESTS_CAPACITY;
    @Value("${ratelimit.tokensRefill}")
    private int TOKENS_REFILL;
    @Value("${ratelimit.refillduration.seconds}")
    private int DURATION_SECONDS;
    private final Map<UserAppID, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Check if the user haven't reached its requests limit
     *
     * @param user the user that a check should be provided for
     * @return true if the user haven't reached limit and is eligible for retrieving payload, false otherwise
     */
    public boolean validateUser(UserAppID user) {
        synchronized (cache) {
            cache.computeIfAbsent(user, userAppID -> getNewBucket());
            log.info("Available tokens for " + user + ": " + cache.get(user).getAvailableTokens());
            return cache.get(user).tryConsume(1);
        }
    }

    Bucket getNewBucket() {
        Refill refill = Refill.intervally(TOKENS_REFILL, Duration.ofSeconds(DURATION_SECONDS));
        return Bucket.builder()
                .addLimit(Bandwidth.classic(REQUESTS_CAPACITY, refill))
                .build();
    }

    public static class OutOfTokensException extends Exception {
    }
}
