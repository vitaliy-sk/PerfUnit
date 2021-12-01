package dev.techh.perfunit.collector;

import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.configuration.data.Rule;
import dev.techh.perfunit.exception.LimitReachedException;
import dev.techh.perfunit.file.FileService;
import dev.techh.perfunit.utils.StackTraceUtils;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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

@Singleton
public class PerfUnitStorage {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

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

    @Inject
    private FileService fileService;

    @Property(name = "reporters.saveTraces", defaultValue = "true")
    private boolean saveTraces;

    public PerfUnitStorage(Configuration configuration) {
        initStorage(configuration);
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

        if ( saveTraces ) {
            saveTrace(limitReachedException, rule);
        }

    }

    private void saveTrace(LimitReachedException limitReachedException, Rule rule) {
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
            File file = new File(fileService.getTempFolder("stack-temp"), String.valueOf(stackTraceId));
            if (file.exists()) return Files.readString(file.toPath());
        } catch (IOException e) {
            LOG.error("Unable to open file", e);
        }
        return saveTraces ? "Unable to load stack" : "Stack saving disabled";
    }


    private void saveStackTrace(long stackTraceId, String stackTrace) {
        try {
            File file = new File(fileService.getTempFolder("stack-temp"), String.valueOf(stackTraceId));
            file.deleteOnExit();
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
