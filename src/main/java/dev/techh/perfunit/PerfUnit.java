package dev.techh.perfunit;

import dev.techh.perfunit.collector.PerfUnitCollector;
import dev.techh.perfunit.configuration.ConfigurationLoader;
import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.transformer.PerfUnitTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;

public class PerfUnit {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void premain(String arguments, Instrumentation instrumentation) {

        LOG.info("\n" +
                "   ___               ___  __  __         _   __ \n" +
                "  / _ \\ ___   ____  / _/ / / / /  ___   (_) / /_\n" +
                " / ___// -_) / __/ / _/ / /_/ /  / _ \\ / / / __/\n" +
                "/_/    \\__/ /_/   /_/   \\____/  /_//_//_/  \\__/ \n");

        if (arguments == null) {
            LOG.error("Please specify config path. -javaagent:perfunit.jar=<CONFIG_PATH>");
            System.exit(-1);
        }

        LOG.info("Starting PerfUnit agent with config {}", arguments);
        Configuration configuration = ConfigurationLoader.load(arguments);
        LOG.info("Loaded config {}", configuration);
        PerfUnitCollector.create(configuration);
        LOG.info("Starting transformations...");
        instrumentation.addTransformer(new PerfUnitTransformer(configuration));
    }

}
