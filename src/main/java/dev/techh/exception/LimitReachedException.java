package dev.techh.exception;

import dev.techh.collector.InvocationsInfo;
import dev.techh.configuration.data.Rule;

import java.util.Map;

public class LimitReachedException extends PerfUnitException {

    private String message;
    private String tracingId;
    private Rule rule;
    private InvocationsInfo invocationsInfo;
    private long executionTime;
    private Map<String, String> metadata;

    public LimitReachedException(String message, String tracingId, Rule rule, InvocationsInfo invocationsInfo,
                                 long lastExecutionTime, Map<String, String> metadata) {
        this.message = message;
        this.tracingId = tracingId;
        this.rule = rule;
        this.invocationsInfo = invocationsInfo;
        this.executionTime = lastExecutionTime;
        this.metadata = metadata;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getTracingId() {
        return tracingId;
    }

    public Rule getRule() {
        return rule;
    }

    public InvocationsInfo getInvocationsInfo() {
        return invocationsInfo;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
//        String failMessage = String.format("Validation failed: %s\n" +
//                        "\t\tInvocation id [%s] failed. Rule id [%s] (%s) \n" +
//                        "\t\tInvocations stat: total count = [%s] total time = [%s] last invoke time = [%s]",
//                ruleFailMessage,
//                getTracingId(rule), rule.getId(), rule.getDescription(),
//                invocationsInfo.getInvocationCount(), invocationsInfo.getTotalTime(), executionTime
//        );

        final StringBuilder sb = new StringBuilder("Validation {");
        sb.append("message='").append(message).append('\'');
        sb.append(", tracingId='").append(tracingId).append('\'');
        sb.append(", rule=").append(rule);
        sb.append(", invocationsInfo=").append(invocationsInfo);
        sb.append(", executionTime=").append(executionTime);
        sb.append('}');
        return sb.toString();
    }
}
