package dev.techh.reporter;

import dev.techh.exception.LimitReachedException;

public interface Reporter {
    void addFailure(LimitReachedException limitReachedException);
}
