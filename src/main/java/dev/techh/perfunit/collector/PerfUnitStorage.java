package dev.techh.perfunit.collector;

import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.configuration.data.Rule;
import dev.techh.perfunit.exception.LimitReachedException;
import dev.techh.perfunit.utils.StackTraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PerfUnitStorage {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private final File tempFolder;

    // <tracing_id:rule_id> = <invocation_info>
    private Map<String, InvocationsInfo> invocationsPerTracing;

    // <rule_id> = <invocation_info>
    private Map<Rule, InvocationsInfo> invocationsPerRule;

    // <rule_id> = <violations_counter>
    private Map<Rule, Integer> violationsPerRule;

    // <rule_id> = < < dump_id,violations_counter >
    private Map<Rule, Map<Long, Integer>> violationsPerStack;

    // <dump_id>
    private Set<Long> stackTracesOnDisk;

    public PerfUnitStorage(Configuration configuration) {
        initStorage(configuration);
        tempFolder = new File("./perfunit-report/temp"); // TODO Move to config
    }

    public InvocationsInfo getInvocation(String tracingId, Rule rule) {
        return invocationsPerTracing.get(getKey(tracingId, rule));
    }

    public void addInvocation(String tracingId, Rule rule, long executionTime) {
        storeInvocation(invocationsPerTracing, getKey(tracingId, rule), executionTime);
        storeInvocation(invocationsPerRule, rule, executionTime);
    }

    public void addFailure(LimitReachedException limitReachedException) {
        Rule rule = limitReachedException.getRule();

        violationsPerRule.put(rule, violationsPerRule.getOrDefault(rule, 0) + 1);

        // TODO Add possibility to disable saving stacks
        String stackTrace = StackTraceUtils.stackTraceToString(limitReachedException);
        long stackTraceId = StackTraceUtils.stackId(stackTrace);

        if (!stackTracesOnDisk.contains(stackTraceId)) {
            saveStackTrace(stackTraceId, stackTrace);
            stackTracesOnDisk.add(stackTraceId);
        }

        Map<Long, Integer> stackCounter = violationsPerStack.computeIfAbsent(rule, (_k) -> new HashMap<>());
        stackCounter.put(stackTraceId, stackCounter.getOrDefault(stackTraceId, 0) + 1);

    }

    public String getStackTrace(long stackTraceId) {
        try {
            File file = new File(tempFolder, String.valueOf(stackTraceId));
            if (file.exists()) return Files.readString(file.toPath());
        } catch (IOException e) {
            LOG.error("Unable to open file", e);
        }
        return "Unable to load stack";
    }


    private void saveStackTrace(long stackTraceId, String stackTrace) {
        try {
            if (!tempFolder.exists()) tempFolder.mkdirs();
            File file = new File(tempFolder, String.valueOf(stackTraceId));
            Files.writeString(file.toPath(), stackTrace);
        } catch (IOException e) {
            LOG.error("Unable to write file", e);
        }
    }

    private <K> void storeInvocation(Map<K, InvocationsInfo> map, K key, long executionTime) {
        InvocationsInfo invocation = map.computeIfAbsent(key, (_x) -> new InvocationsInfo());
        invocation.addInvocation();
        invocation.addTime(executionTime);
    }

    private String getKey(String tracingId, Rule rule) {
        return tracingId + ":" + rule.getId();
    }

    private void initStorage(Configuration configuration) {
        long maxEntries = configuration.getStorageLimit();

        invocationsPerTracing = maxEntries > 0 ? createStorageWithLimit(maxEntries) : unlimitedStorage();
        invocationsPerRule = unlimitedStorage();

        violationsPerRule = unlimitedStorage();
        violationsPerStack = unlimitedStorage();

        stackTracesOnDisk = new HashSet<>();
    }

    private <K, V> Map<K, V> createStorageWithLimit(final long maxEntries) {
        return Collections.synchronizedMap(
                new LinkedHashMap<K, V>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                        return size() > maxEntries;
                    }
                });
    }

    private <K, V> Map<K, V> unlimitedStorage() {
        return new ConcurrentHashMap<>();
    }

    public Map<String, InvocationsInfo> getInvocationsPerTracing() {
        return invocationsPerTracing;
    }

    public Map<Rule, InvocationsInfo> getInvocationsPerRule() {
        return invocationsPerRule;
    }

    public Map<Rule, Integer> getViolationsPerRule() {
        return violationsPerRule;
    }

    public Map<Rule, Map<Long, Integer>> getViolationsPerStack() {
        return violationsPerStack;
    }

    public Set<Long> getStackTracesOnDisk() {
        return stackTracesOnDisk;
    }

}
