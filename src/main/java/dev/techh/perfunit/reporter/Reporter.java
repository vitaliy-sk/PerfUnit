package dev.techh.perfunit.reporter;

import dev.techh.perfunit.exception.LimitReachedException;

public interface Reporter {
    void onFailure(LimitReachedException limitReachedException);
    default void save() {}
}
