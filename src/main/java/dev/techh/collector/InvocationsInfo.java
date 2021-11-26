package dev.techh.collector;

import java.util.concurrent.atomic.AtomicLong;

public class InvocationsInfo {

    private final String ruleId;
    private final String tracingId;

    private final AtomicLong invocationCount = new AtomicLong(0);
    private final AtomicLong totalTime = new AtomicLong(0);

    public InvocationsInfo(String ruleId, String tracingId) {
        this.ruleId = ruleId;
        this.tracingId = tracingId;
    }

    public String getRuleId() {
        return ruleId;
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
        sb.append("ruleId='").append(ruleId).append('\'');
        sb.append(", tracingId='").append(tracingId).append('\'');
        sb.append(", invocationCount=").append(invocationCount);
        sb.append(", totalTime=").append(totalTime);
        sb.append('}');
        return sb.toString();
    }
}
