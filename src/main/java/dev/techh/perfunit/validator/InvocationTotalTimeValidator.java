package dev.techh.perfunit.validator;

import dev.techh.perfunit.collector.InvocationsInfo;
import dev.techh.perfunit.configuration.data.Rule;

import java.util.Optional;

public class InvocationTotalTimeValidator implements RuleValidator {

    @Override
    public Optional<String> validate(Rule rule, InvocationsInfo invocationsInfo, long executionTime) {
        long limit = rule.getLimit().getTimeTotal();
        long totalTime = invocationsInfo.getTotalTime();

        return totalTime > limit ? Optional.of(String.format("Total invocation time [%s] > [%s]", totalTime, limit)) :
                Optional.empty();
    }

    @Override
    public boolean support(Rule rule) {
        return rule.getLimit().getTimeTotal() > -1;
    }


}
