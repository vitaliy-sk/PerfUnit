package dev.techh.perfunit.reporter;

import io.micronaut.context.annotation.Property;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class DiskSaveReportRunnable implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Property(name = "reporters.periodicallySaveReportToDisk", defaultValue = "-1")
    private long periodicallySaveReportToDisk;

    @Inject
    private Collection<Reporter> reporters;

    @Override
    public void run() {
        reporters.forEach(Reporter::save);
    }

    @PostConstruct
    public void init() {

        if ( periodicallySaveReportToDisk > 0 ) {
            LOG.info("Run perf unit report saver");
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this, periodicallySaveReportToDisk, periodicallySaveReportToDisk, TimeUnit.MILLISECONDS);
        }

    }

}
