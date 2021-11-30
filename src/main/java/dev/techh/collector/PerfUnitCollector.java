package dev.techh.collector;

import dev.techh.configuration.data.Configuration;
import dev.techh.configuration.data.Rule;
import dev.techh.exception.LimitReachedException;
import dev.techh.exception.UnknownCallerException;
import dev.techh.reporter.ConsoleReporter;
import dev.techh.reporter.Reporter;
import dev.techh.validator.InvocationCountValidator;
import dev.techh.validator.InvocationTotalTimeValidator;
import dev.techh.validator.RuleValidator;
import dev.techh.validator.SingleInvocationTimeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class PerfUnitCollector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static PerfUnitCollector INSTANCE;

    private final Configuration configuration;
    private final PerfUnitStorage storage;

    private final RuleValidator[] validators;
    private final Reporter[] reporters = {new ConsoleReporter()}; // TODO Load from yaml

    public static void create(Configuration configuration) {
        if (INSTANCE == null) INSTANCE = new PerfUnitCollector(configuration);
    }

    private PerfUnitCollector(Configuration configuration) {
        this.configuration = configuration;
        this.storage = new PerfUnitStorage(configuration.getStorageLimit());
        this.validators = new RuleValidator[]{new InvocationCountValidator(), new InvocationTotalTimeValidator(),
                new SingleInvocationTimeValidator()};
    }

    @SuppressWarnings("unused") // Used in client application
    public static PerfUnitCollector getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unused") // Used in client application
    public void onInvoke(String ruleId, String methodSignature, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;

        Map<String, String> mdc = Optional.ofNullable(MDC.getCopyOfContextMap()).orElse(Collections.emptyMap());
        mdc.forEach((key, value) -> LOG.trace("\t{}={}", key, value));

        Rule rule = configuration.getRules().get(ruleId);
        String tracingId = getTracingId(rule);

        if (tracingId == null) {
            String message = String.format("MDC doesn't have [%s] for tracing. MDC: [%s]", rule.getTracingKey(), mdc);
            if (!rule.isAllowUnknownCalls()) throw new UnknownCallerException(message);
            return;
        } else {
            LOG.trace("Call [{}] rule [{}] thread [{}] took [{}] msec", methodSignature, rule.getTracingKey(),
                    Thread.currentThread().getName(), executionTime);
        }

        InvocationsInfo invocationsInfo = captureInvocation(tracingId, rule, executionTime);
        LOG.trace("{}", invocationsInfo);

        validate(mdc, rule, invocationsInfo, executionTime);

    }

    private void validate(Map<String, String> mdc, Rule rule, InvocationsInfo invocationsInfo, long executionTime) {

        for (RuleValidator ruleValidator : validators) {
            if (!ruleValidator.support(rule)) continue;

            Optional<String> ruleFailMessage = ruleValidator.validate(rule, invocationsInfo, executionTime);
            if (ruleFailMessage.isEmpty()) continue;

            failValidation(mdc, rule, invocationsInfo, executionTime, ruleFailMessage.get());
        }

    }

    private void failValidation(Map<String, String> mdc, Rule rule, InvocationsInfo invocationsInfo, long executionTime,
                                String ruleFailMessage) {
        LimitReachedException limitReachedException = new LimitReachedException(ruleFailMessage, getTracingId(rule), rule, invocationsInfo, executionTime, mdc);
        Arrays.stream( reporters ).forEach( reporter ->  reporter.addFailure( limitReachedException ));

        if (!rule.isAllowFail()) {
            throw limitReachedException;
        }
    }

    private InvocationsInfo captureInvocation(String tracingId, Rule rule, long executionTime) {
        InvocationsInfo invocationsInfo = storage.getInfo(rule, tracingId);
        invocationsInfo.addInvocation();
        invocationsInfo.addTime(executionTime);
        return invocationsInfo;
    }

    private String getTracingId(Rule rule) {
        return MDC.get(rule.getTracingKey());
    }

}
