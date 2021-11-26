package dev.techh.validator;

import dev.techh.collector.InvocationsInfo;
import dev.techh.configuration.data.Rule;

import java.util.Optional;

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
