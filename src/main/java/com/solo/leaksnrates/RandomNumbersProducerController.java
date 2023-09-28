package com.solo.leaksnrates;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.stream.IntStream;

@Log
@RestController
public class RandomNumbersProducerController {
    private final Random randomGenerator = new Random();
    private static final String LOG_REQUEST = "User requested %d numbers. Response: ";
    @Autowired
    FaultyStorage storage;

    @GetMapping(path = "/random", params = {"count"})
    ResponseEntity<String> getRandomNumbers(@RequestParam Integer count) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append('{').append(randomGenerator.nextInt());
            IntStream.range(0, count).forEach(v -> sb.append(',').append(randomGenerator.nextInt()));
            sb.append('}');
            storage.addRequest(String.format(LOG_REQUEST, count) + sb);
            return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Failed to process request, reason: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
