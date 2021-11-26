package dev.techh.integration;

import dev.techh.exception.LimitReachedException;
import dev.techh.exception.UnknownCallerException;
import dev.techh.integration.service.ExpensiveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgentIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExpensiveService expensiveService = new ExpensiveService();

    @BeforeEach
    public void setUp() {
        MDC.put("traceId", "AgentIntegrationTest-" + UUID.randomUUID());
    }

    @Test
    public void shouldReachInvocationsLimits() {
        LimitReachedException exception = assertThrows(LimitReachedException.class, () -> {
            for (int invocation = 0; invocation < 6; invocation++) {
                expensiveService.count5InvocationsAllowed();
            }
        });

        assertThat(exception.getMessage(), startsWith("Validation failed: Total invocation count [6] > [5]"));
    }

    @Test
    public void shouldNotReachInvocationsLimits() {
        assertDoesNotThrow(() -> {
            for (int invocation = 0; invocation < 5; invocation++) {
                expensiveService.count5InvocationsAllowed();
            }
        });
    }

    @Test
    public void shouldReachSingleExecutionTimeLimit() {

        expensiveService.time10MsecSingleAllowed(0);

        LimitReachedException exception = assertThrows(LimitReachedException.class, () -> {
            for (int invocation = 0; invocation < 6; invocation++) {
                expensiveService.time10MsecSingleAllowed(11);
            }
        });

        assertThat(exception.getMessage(), startsWith("Validation failed: Single invocation time"));
    }

    @Test
    public void shouldNotReachSingleExecutionTimeLimit() {
        assertDoesNotThrow(() -> {
            for (int invocation = 0; invocation < 5; invocation++) {
                expensiveService.time10MsecSingleAllowed(0);
            }
        });
    }

    @Test
    public void shouldReachTotalExecutionTimeLimit() {

        LimitReachedException exception = assertThrows(LimitReachedException.class, () -> {
            for (int invocation = 0; invocation < 20; invocation++) {
                expensiveService.time10MsecTotalAllowed(5);
            }
        });

        assertThat(exception.getMessage(), startsWith("Validation failed: Total invocation time"));
    }

    @Test
    public void shouldNotReachTotalExecutionTimeLimit() {

        assertDoesNotThrow(() -> {
            for (int invocation = 0; invocation < 3; invocation++) {
                expensiveService.time10MsecTotalAllowed(2);
            }
        });

    }

    @Test
    public void shouldNotAllowUnknownCallers() {

        MDC.clear();

        UnknownCallerException exception = assertThrows(UnknownCallerException.class,
                expensiveService::unknownCallerNotAllowed);

        assertThat(exception.getMessage(), startsWith("MDC doesn't have [traceId] for tracing"));
    }

    @Test
    public void shouldAllowUnknownCallers() {

        MDC.clear();
        assertDoesNotThrow(expensiveService::unknownCallerAllowed);

    }

    @Test
    public void shouldAllowFail() {
        assertDoesNotThrow(expensiveService::count0InvocationsAllowedNotFail);
    }

}
