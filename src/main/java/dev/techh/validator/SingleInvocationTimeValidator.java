package dev.techh.validator;

import dev.techh.collector.InvocationsInfo;
import dev.techh.configuration.data.Rule;

import java.util.Optional;

public class SingleInvocationTimeValidator implements RuleValidator {

    @Override
    public Optional<String> validate(Rule rule, InvocationsInfo invocationsInfo, long executionTime) {
        long limit = rule.getLimit().getTimeSingle();

        return executionTime > limit ? Optional.of(String.format("Single invocation time [%s] > [%s]", executionTime, limit)) :
                Optional.empty();
    }

    @Override
    public boolean support(Rule rule) {
        return rule.getLimit().getTimeSingle() > -1;
    }

}
