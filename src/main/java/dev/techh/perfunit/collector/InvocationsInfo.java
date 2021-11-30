package dev.techh.perfunit.collector;

import dev.techh.perfunit.configuration.data.Rule;

import java.util.concurrent.atomic.AtomicLong;

public class InvocationsInfo {

    private final Rule rule;
    private final String tracingId;

    private final AtomicLong invocationCount = new AtomicLong(0);
    private final AtomicLong totalTime = new AtomicLong(0);

    public InvocationsInfo(Rule rule, String tracingId) {
        this.rule = rule;
        this.tracingId = tracingId;
    }

    public Rule getRule() {
        return rule;
    }

    public String getTracingId() {
        return tracingId;
    }

    public long getInvocationCount() {
        return invocationCount.get();
    }

    public long getTotalTime() {
        return totalTime.get();
    }

    public long addInvocation() {
       return invocationCount.addAndGet(1L);
    }

    public long addTime(long time) {
        return totalTime.addAndGet(time);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Invocation{");
        sb.append("ruleId='").append(rule.getId()).append('\'');
        sb.append(", tracingId='").append(tracingId).append('\'');
        sb.append(", invocationCount=").append(invocationCount);
        sb.append(", totalTime=").append(totalTime);
        sb.append('}');
        return sb.toString();
    }
}
