package com.solo.leaksnrates;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.time.Duration;

public class RateLimitProvider {
    public static final int REQUESTS_CAPACITY = 10;
    public static final int TOKENS_REFILL = 5;
    private static final Refill refill = Refill.intervally(TOKENS_REFILL, Duration.ofSeconds(20));
    private static final Bandwidth limit = Bandwidth.classic(REQUESTS_CAPACITY, refill);

    private RateLimitProvider() {
    }

    public static Bucket getNewBucket() {
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public static class OutOfTokensException extends Throwable {
    }
}
