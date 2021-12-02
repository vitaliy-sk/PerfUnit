package dev.techh.perfunit.collector;

import java.util.Comparator;

public class InvocationsViolationsComparator implements Comparator<InvocationsInfo> {

    @Override
    public int compare(InvocationsInfo o1, InvocationsInfo o2) {
        return Long.compare(o1.getViolationsCounter().longValue(), o2.getViolationsCounter().longValue());
    }

    public static Comparator<InvocationsInfo> reverseOrder() {
        return new InvocationsViolationsComparator().reversed();
    }

}
