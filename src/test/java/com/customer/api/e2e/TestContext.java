package com.customer.api.e2e;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestContext {

    private final Map<String, Object> data = new HashMap<>();

    public void set(String key, Object value) { data.put(key, value); }
    public void put(String key, Object value) { data.put(key, value); }
    public Object get(String key) { return data.get(key); }
    public <T> T get(String key, Class<T> type) { return type.cast(data.get(key)); }
    public void clear() { data.clear(); }
}
