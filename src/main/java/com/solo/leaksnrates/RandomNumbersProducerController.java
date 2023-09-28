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

    @GetMapping(path = "/random", params = {"count"})
    ResponseEntity<String> getRandomNumbers(@RequestParam Integer count) {
        try {
            return new ResponseEntity<>(service.getRandomNumbers(count), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Failed to process request, reason: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
    }

    @GetMapping(path = "/throttle_random", params = {"count"})
    ResponseEntity<String> getRandomNumbersThrottled(@RequestHeader(value = "x-tr-uuid") String uuid,
                                                     @RequestHeader(value = "x-tr-applicationid") String appId,
                                                     @RequestParam Integer count) {

        try {
            return new ResponseEntity<>(service.getRandomNumbersThrottled(new UserAppID(uuid, appId), count), HttpStatus.OK);
        } catch (OutOfTokensException e) {
            log.info(String.format("Throttled %s request from application: %s", uuid, appId));
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        } catch (Exception e) {
            log.info("Failed to process request, reason: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
