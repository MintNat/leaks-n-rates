package com.solo.leaksnrates;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.solo.leaksnrates.RateLimitProvider.OutOfTokensException;

@Log
@RestController
public class RandomNumbersProducerController {
    @Autowired
    RandomNumberService service;

    /**
     * Provides random numbers as much as user requested.
     *
     * @param count how many numbers should be generated
     * @return response with the random numbers
     * @implNote Produces memory leak by logging each request and response.
     */
    @GetMapping(path = "/random", params = {"count"})
    ResponseEntity<String> getRandomNumbers(@RequestParam Integer count) {
        try {
            return new ResponseEntity<>(service.getRandomNumbers(count), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Failed to process request, reason: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
    }

    /**
     * Provides random numbers as much as user requested with the rate limiting based on headers.
     *
     * @param uuid a header with the user ID
     * @param appId a header with the calling application ID
     * @param count how many numbers should be generated
     * @return 200 response with generated random numbers or 429 if the request was throttled
     * @implNote Produces memory leak by logging each request and response.
     */
    @GetMapping(path = "/throttle_random", params = {"count"})
    ResponseEntity<String> getRandomNumbersThrottled(@RequestHeader(value = "x-tr-uuid") String uuid,
                                                     @RequestHeader(value = "x-tr-applicationid") String appId,
                                                     @RequestParam Integer count) {

        UserAppID user = new UserAppID(uuid, appId);
        try {
            return new ResponseEntity<>(service.getRandomNumbersThrottled(user, count), HttpStatus.OK);
        } catch (OutOfTokensException e) {
            log.info(String.format("Throttled request from %s", user));
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        } catch (Exception e) {
            log.info("Failed to process request, reason: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
