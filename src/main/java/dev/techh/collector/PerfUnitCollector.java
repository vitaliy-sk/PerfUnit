package dev.techh.collector;

import dev.techh.configuration.data.Configuration;
import dev.techh.configuration.data.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class PerfUnitCollector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static PerfUnitCollector INSTANCE;

    private final Configuration configuration;
    private final PerfUnitStorage storage;

    public static void create(Configuration configuration) {
        if (INSTANCE == null) INSTANCE = new PerfUnitCollector(configuration);
    }

    private PerfUnitCollector(Configuration configuration) {
        this.configuration = configuration;
        this.storage = new PerfUnitStorage(configuration.getStorageLimit());
    }

    public static PerfUnitCollector getInstance() {
        return INSTANCE;
    }

    public void onInvoke(String ruleId, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;

        LOG.trace("Call [{}] thread [{}] took [{}] msec", ruleId, Thread.currentThread().getName(), executionTime);

        Map<String, String> mdc = MDC.getCopyOfContextMap();
        mdc.forEach((key, value) -> LOG.trace("\t{}={}", key, value));

        Rule rule = configuration.getRules().get(ruleId);
        String tracingIdKey = rule.getLimit().getKey();

        if (!mdc.containsKey(tracingIdKey)) {
            LOG.warn("MDC doesn't have [{}] for tracing ", tracingIdKey);
            return;
        }

        String tracingId = mdc.get(tracingIdKey);
        InvocationsInfo invocationsInfo = captureInvocation(ruleId, executionTime, tracingId);
        LOG.trace("{}", invocationsInfo);

        // TODO Validate invocation info with validators set

    }

    private InvocationsInfo captureInvocation(String ruleId, long executionTime, String tracingId) {
        InvocationsInfo invocationsInfo = storage.getInfo(ruleId, tracingId);
        invocationsInfo.addInvocation();
        invocationsInfo.addTime(executionTime);
        return invocationsInfo;
    }

}
