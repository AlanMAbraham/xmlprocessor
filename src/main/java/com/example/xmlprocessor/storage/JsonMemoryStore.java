package com.example.xmlprocessor.storage;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class JsonMemoryStore {

    private final ConcurrentMap<UUID, String> files =
            new ConcurrentHashMap<>();

    public void save(UUID fileId, String json) {
        files.put(fileId, json);
    }

    public Optional<String> find(UUID fileId) {
        return Optional.ofNullable(files.get(fileId));
    }

    public void remove(UUID fileId) {
        files.remove(fileId);
    }
}