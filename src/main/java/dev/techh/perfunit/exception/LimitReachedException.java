package dev.techh.perfunit.exception;

import dev.techh.perfunit.collector.InvocationsInfo;
import dev.techh.perfunit.configuration.data.Rule;

import java.util.Map;

public class LimitReachedException extends PerfUnitException {

    private final String message;
    private final String tracingId;
    private final Rule rule;
    private final InvocationsInfo invocationsInfo;
    private final long executionTime;
    private final Map<String, String> metadata;

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
