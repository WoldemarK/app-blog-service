package ru.yandex.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class InMemoryIdempotencyService {

    private final Map<String, Object> storage = new ConcurrentHashMap<>();

    public <T> Optional<T> get(String key) {
        return Optional.ofNullable((T) storage.get(key));
    }

    public <T> void put(String key, T value) {
        storage.put(key, value);
    }

    public synchronized <T> T execute(String key, Supplier<T> action) {
        Optional<T> cached = get(key);
        if (cached.isPresent()) {
            return cached.get();
        }
        T result = action.get();
        put(key, result);
        return result;
    }
}
