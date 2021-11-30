package dev.techh.perfunit.reporter;

import dev.techh.perfunit.exception.LimitReachedException;

public interface Reporter {
    void addFailure(LimitReachedException limitReachedException);
}
