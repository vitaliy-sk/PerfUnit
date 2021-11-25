package dev.techh.integration;

import dev.techh.integration.service.ExpensiveService;
import org.slf4j.MDC;

import java.util.Random;
import java.util.UUID;

public class AgentTest {

    private ExpensiveService expensiveService = new ExpensiveService();

    public String testNoArgs() {
        return expensiveService.testNoArgs();
    }

    public String testWithArgs(String arg) {
        return expensiveService.testWithArgs(arg);
    }

    public static void main(String[] args) {

        int invocations = args.length > 0 ? Integer.parseInt( args[0] ) : 10;

        System.out.println("This is an test app");

        MDC.put("trace-id", UUID.randomUUID().toString());
        MDC.put("span-id", UUID.randomUUID().toString());

        AgentTest agentTest = new AgentTest();

        for (int i = 0; i < invocations; i++) { agentTest.testNoArgs(); }
        for (int i = 0; i < invocations; i++) { agentTest.testWithArgs(String.format("Invocation: %d", i)); }

    }

}
