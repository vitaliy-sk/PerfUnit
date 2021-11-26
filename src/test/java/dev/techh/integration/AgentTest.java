package dev.techh.integration;

import dev.techh.integration.service.ExpensiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

public class AgentTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {

        int invocations = args.length > 0 ? Integer.parseInt( args[0] ) : 10;
        int requests = args.length > 1 ? Integer.parseInt( args[0] ) : 10;

        LOG.info("This is an test app");

        ExpensiveService expensiveService = new ExpensiveService();

        while (requests >= 0) {
            MDC.put("traceId", String.format("request-%d", requests));
            for (int i = 0; i < invocations; i++) { expensiveService.testWithDelay(150); }
            for (int i = 0; i < invocations; i++) { expensiveService.testNoArgs(); }
            for (int i = 0; i < invocations; i++) { expensiveService.testWithArgs(String.format("Invocation: %d", i)); }
            requests--;
        }

    }

}
