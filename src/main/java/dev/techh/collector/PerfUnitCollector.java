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

    public void onInvoke(String method) {
        LOG.info("Call {} thread {}", method, Thread.currentThread().getName());
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        mdc.forEach((key, value) -> LOG.info("\t{}={}", key, value));
    }

}
