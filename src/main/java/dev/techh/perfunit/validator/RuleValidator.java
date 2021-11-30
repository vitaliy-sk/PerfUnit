package dev.techh.perfunit.validator;

import dev.techh.perfunit.collector.InvocationsInfo;
import dev.techh.perfunit.configuration.data.Rule;

import java.util.Optional;

public interface RuleValidator {

    /**
     *
     * @param rule
     * @param invocationsInfo
     * @param executionTime
     * @return Error message
     */
    Optional<String> validate(Rule rule, InvocationsInfo invocationsInfo, long executionTime);
    boolean support(Rule rule);

}
