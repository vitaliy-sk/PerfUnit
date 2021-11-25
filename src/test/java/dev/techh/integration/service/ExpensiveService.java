package dev.techh.integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

public class ExpensiveService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public String testNoArgs() {
        LOG.info("Test no args invoked. MDC = {}", MDC.getCopyOfContextMap());
        return "Test No Args";
    }

    public String testWithArgs(String arg) {
        LOG.info("Test with arg = {} invoked. MDC = {}", arg, MDC.getCopyOfContextMap());
        return "Test: " + arg;
    }

}
