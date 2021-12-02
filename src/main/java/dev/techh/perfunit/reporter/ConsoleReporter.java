package dev.techh.perfunit.reporter;

import dev.techh.perfunit.exception.LimitReachedException;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Singleton
@Requires(property = "reporters.markdown.enable", value = "true")
public class ConsoleReporter implements Reporter {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Property(name = "reporters.console.printTrace", defaultValue = "false")
    private boolean printTrace;

    @Override
    public void onFailure(LimitReachedException limitReachedException) {

        if (printTrace) {
            LOG.error("", limitReachedException);
        } else {
            LOG.error(limitReachedException.toString());
        }

    }

}
