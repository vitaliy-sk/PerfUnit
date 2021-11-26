package dev.techh.integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

public class ExpensiveService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public String testNoArgs() {
        return "Test No Args";
    }

    public String testWithArgs(String arg) {
        return "Test: " + arg;
    }

    public String testWithDelay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return " 1";
    }

}
