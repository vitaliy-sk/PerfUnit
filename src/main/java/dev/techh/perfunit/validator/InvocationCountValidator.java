package dev.techh.perfunit.validator;

import dev.techh.perfunit.collector.InvocationsInfo;
import dev.techh.perfunit.configuration.data.Rule;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class InvocationCountValidator implements RuleValidator {

    @Override
    public Optional<String> validate(Rule rule, InvocationsInfo invocationsInfo, long executionTime) {
        long limit = rule.getLimit().getCount();
        long invocationCount = invocationsInfo.getInvocationCount();

        return invocationCount > limit ? Optional.of(String.format("Total invocation count [%s] > [%s]", invocationCount, limit)) : Optional.empty();
    }

    @Override
    public boolean support(Rule rule) {
        return rule.getLimit().getCount() > -1;
    }

}
