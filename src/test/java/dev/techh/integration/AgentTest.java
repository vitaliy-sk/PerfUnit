package dev.techh.integration;

import dev.techh.integration.service.ExpensiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

public class AgentTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ExpensiveService expensiveService = new ExpensiveService();

    public String testNoArgs() {
        return expensiveService.testNoArgs();
    }

    public String testWithArgs(String arg) {
        return expensiveService.testWithArgs(arg);
    }

    public static void main(String[] args) {

        int invocations = args.length > 0 ? Integer.parseInt( args[0] ) : 10;

        LOG.info("This is an test app");

        MDC.put("traceId", UUID.randomUUID().toString());

        AgentTest agentTest = new AgentTest();

        for (int i = 0; i < invocations; i++) { agentTest.testNoArgs(); }
        for (int i = 0; i < invocations; i++) { agentTest.testWithArgs(String.format("Invocation: %d", i)); }

    }

}
