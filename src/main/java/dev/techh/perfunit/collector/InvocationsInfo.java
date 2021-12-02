package dev.techh.perfunit.collector;

import java.util.concurrent.atomic.AtomicLong;

public class InvocationsInfo {

    private final AtomicLong invocationCount = new AtomicLong(0);
    private final AtomicLong totalTime = new AtomicLong(0);
    private final AtomicLong violationsCounter = new AtomicLong(0);

    public long getInvocationCount() {
        return invocationCount.get();
    }

    public long getTotalTime() {
        return totalTime.get();
    }

    public AtomicLong getViolationsCounter() {
        return violationsCounter;
    }

    public long addViolation() {
        return violationsCounter.addAndGet(1L);
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
        sb.append("invocationCount=").append(invocationCount);
        sb.append(", totalTime=").append(totalTime);
        sb.append('}');
        return sb.toString();
    }
}
