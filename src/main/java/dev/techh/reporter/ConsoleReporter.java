package dev.techh.reporter;

import dev.techh.configuration.data.Rule;
import dev.techh.exception.LimitReachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ConsoleReporter implements Reporter {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void addFailure(LimitReachedException limitReachedException) {

        Rule rule = limitReachedException.getRule();

        // TODO Move to console reporter config section
        if (rule.isPrintTrace()) {
            LOG.error("", limitReachedException);
        } else {
            LOG.error(limitReachedException.toString());
        }

    }

}
