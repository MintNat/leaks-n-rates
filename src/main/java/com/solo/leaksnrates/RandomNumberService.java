package com.solo.leaksnrates;

import io.github.bucket4j.Bucket;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static com.solo.leaksnrates.RateLimitProvider.OutOfTokensException;
import static com.solo.leaksnrates.RateLimitProvider.getNewBucket;

@Log
@Service
public class RandomNumberService {
    private final Random randomGenerator = new Random();
    private static final String LOG_REQUEST = "User requested %d numbers. Response: ";
    private final Map<UserAppID, Bucket> cache = new ConcurrentHashMap<>();
    @Autowired
    FaultyStorage storage;

    String getRandomNumbers(int count) {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(randomGenerator.nextInt());
        IntStream.range(0, count).forEach(v -> sb.append(',').append(randomGenerator.nextInt()));
        sb.append('}');
        storage.addRequest(String.format(LOG_REQUEST, count) + sb);
        return sb.toString();
    }

    String getRandomNumbersThrottled(UserAppID user, int count) throws OutOfTokensException {
        synchronized (cache) {
            cache.computeIfAbsent(user, userAppID -> getNewBucket());
            log.info("Available tokens for user" + user + ": " + cache.get(user).getAvailableTokens());
            if (cache.get(user).tryConsume(1))
                return getRandomNumbers(count);
            throw new OutOfTokensException();
        }
    }
}
