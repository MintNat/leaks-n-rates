package com.solo.leaksnrates;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log
@Component
public class FaultyStorage {
    private final Map<String, Integer> map = new HashMap<>();

    void addRequest(String request) {
        synchronized (map) {
            map.put(request, map.getOrDefault(request, 0) + 1);
        }
    }

    void freeSpace() {
        map.clear();
    }
}
