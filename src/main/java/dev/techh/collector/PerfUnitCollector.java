package dev.techh.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class PerfUnitCollector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final PerfUnitCollector INSTANCE = new PerfUnitCollector();

    public static PerfUnitCollector getInstance() {
        return INSTANCE;
    }

    public void onInvoke(String ruleKey, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;
        LOG.trace("Call [{}] thread [{}] took [{}] msec", ruleKey, Thread.currentThread().getName(), executionTime);
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        mdc.forEach((key, value) -> LOG.trace("\t{}={}", key, value));
    }

}
