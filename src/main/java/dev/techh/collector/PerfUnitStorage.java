package dev.techh.collector;

import dev.techh.configuration.data.Rule;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerfUnitStorage {

    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Map<String, InvocationsInfo> data;

    public PerfUnitStorage (long maxEntries) {
        if ( maxEntries > 0 ) {
            data = createStorageWithLimit(maxEntries);
        } else {
            data = unlimitedStorage();
        }
    }

    public InvocationsInfo getInfo(Rule rule, String tracingId ) {
        String key = tracingId + ":" + rule.getId();
        return data.computeIfAbsent( key, (_x) -> new InvocationsInfo(rule, tracingId) );
    }

    private Map<String, InvocationsInfo> createStorageWithLimit(final long maxEntries) {
        return Collections.synchronizedMap(new LinkedHashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, InvocationsInfo> eldest) {
                return size() > maxEntries;
            }
        });
    }

    private ConcurrentHashMap<String, InvocationsInfo> unlimitedStorage() {
        return new ConcurrentHashMap<>();
    }

}
