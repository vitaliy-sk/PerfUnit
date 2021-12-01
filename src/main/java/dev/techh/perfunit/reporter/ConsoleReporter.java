package dev.techh.perfunit.reporter;

import dev.techh.perfunit.configuration.data.Rule;
import dev.techh.perfunit.exception.LimitReachedException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Singleton
public class ConsoleReporter implements Reporter {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void onFailure(LimitReachedException limitReachedException) {

        Rule rule = limitReachedException.getRule();

        // TODO Move to console reporter config section
        if (rule.isPrintTrace()) {
            LOG.error("", limitReachedException);
        } else {
            LOG.error(limitReachedException.toString());
        }

    }

}
