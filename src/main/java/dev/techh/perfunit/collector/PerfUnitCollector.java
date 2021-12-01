package dev.techh.perfunit.collector;

import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.configuration.data.Rule;
import dev.techh.perfunit.exception.LimitReachedException;
import dev.techh.perfunit.exception.UnknownCallerException;
import dev.techh.perfunit.reporter.Reporter;
import dev.techh.perfunit.utils.ContextHolder;
import dev.techh.perfunit.validator.RuleValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Singleton
public class PerfUnitCollector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    private Configuration configuration;

    @Inject
    private PerfUnitStorage storage;

    @Inject
    private Collection<RuleValidator> validators;

    @Inject
    private Collection<Reporter> reporters;

    @SuppressWarnings("unused") // Used in client application
    public static PerfUnitCollector getInstance() {
        return ContextHolder.getContext().getBean(PerfUnitCollector.class);
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

        storage.addInvocation(tracingId, rule, executionTime);
        InvocationsInfo tracingInvocation = storage.getInvocation(tracingId, rule);
        LOG.trace("{}", tracingInvocation);

        validate(mdc, rule, tracingInvocation, executionTime);

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
        LimitReachedException
                limitReachedException = new LimitReachedException(ruleFailMessage, getTracingId(rule), rule, invocationsInfo, executionTime, mdc);

        storage.addFailure( limitReachedException );
        reporters.forEach( reporter ->  reporter.onFailure( limitReachedException ));

        if (!rule.isAllowFail()) {
            throw limitReachedException;
        }
    }

    private String getTracingId(Rule rule) {
        return MDC.get(rule.getTracingKey());
    }

}
