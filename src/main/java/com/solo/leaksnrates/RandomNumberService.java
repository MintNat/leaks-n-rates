package com.solo.leaksnrates;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.IntStream;

import static com.solo.leaksnrates.RateLimitProvider.OutOfTokensException;

@Log
@Service
class RandomNumberService {
    private final Random randomGenerator = new Random();
    private static final String LOG_REQUEST = "User requested %d numbers. Response: ";
    @Autowired
    RateLimitProvider limitProvider;
    @Autowired
    FaultyStorage logStorage;

    String getRandomNumbers(int count) {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(randomGenerator.nextInt());
        IntStream.range(0, count).forEach(v -> sb.append(',').append(randomGenerator.nextInt()));
        sb.append('}');
        logStorage.addRequest(String.format(LOG_REQUEST, count) + sb);
        return sb.toString();
    }

    String getRandomNumbersThrottled(UserAppID user, int count) throws OutOfTokensException {
        if (!limitProvider.validateUser(user)) throw new OutOfTokensException();
        return getRandomNumbers(count);
    }
}
