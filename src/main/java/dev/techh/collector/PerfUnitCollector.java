package dev.techh.collector;

import dev.techh.configuration.data.Configuration;
import dev.techh.configuration.data.Rule;
import dev.techh.exception.LimitReachedException;
import dev.techh.validator.InvocationCountValidator;
import dev.techh.validator.InvocationTotalTimeValidator;
import dev.techh.validator.RuleValidator;
import dev.techh.validator.SingleInvocationTimeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

public class PerfUnitCollector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static PerfUnitCollector INSTANCE;

    private final Configuration configuration;
    private final PerfUnitStorage storage;
    private final RuleValidator[] validators;

    public static void create(Configuration configuration) {
        if (INSTANCE == null) INSTANCE = new PerfUnitCollector(configuration);
    }

    private PerfUnitCollector(Configuration configuration) {
        this.configuration = configuration;
        this.storage = new PerfUnitStorage(configuration.getStorageLimit());
        this.validators = new RuleValidator[] { new InvocationCountValidator(), new InvocationTotalTimeValidator(), new SingleInvocationTimeValidator() };
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
        String tracingIdKey = rule.getKey();

        if (!mdc.containsKey(tracingIdKey)) {
            LOG.warn("MDC doesn't have [{}] for tracing ", tracingIdKey);
            return;
        }

        String tracingId = mdc.get(tracingIdKey);
        InvocationsInfo invocationsInfo = captureInvocation(ruleId, executionTime, tracingId);
        LOG.trace("{}", invocationsInfo);

        validate(tracingId, rule, invocationsInfo, executionTime);

    }

    private void validate(String tracingId, Rule rule, InvocationsInfo invocationsInfo, long executionTime) {

        for ( RuleValidator ruleValidator : validators ) {
            if (!ruleValidator.support(rule)) continue;

            Optional<String> error = ruleValidator.validate(rule, invocationsInfo, executionTime);
            if (error.isEmpty()) continue;

            String failMessage = String.format( "Validation failed: %s\n" +
                    "\t\tInvocation [%s] (%s) failed\n" +
                    "\t\tInvocations stat: total count = [%s] total time = [%s] last invoke time = [%s]",
                    error.get(),
                    tracingId, rule.getDescription(),
                    invocationsInfo.getInvocationCount(), invocationsInfo.getTotalTime(), executionTime
                    );

            LOG.error(failMessage);

            if (rule.isPrintTrace()) {
                // TODO Print stack
            }

            if (!rule.isAllowFail()) {
              throw new LimitReachedException(failMessage);
            }
        }

    }

    private InvocationsInfo captureInvocation(String ruleId, long executionTime, String tracingId) {
        InvocationsInfo invocationsInfo = storage.getInfo(ruleId, tracingId);
        invocationsInfo.addInvocation();
        invocationsInfo.addTime(executionTime);
        return invocationsInfo;
    }

}
