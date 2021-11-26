package dev.techh;

import dev.techh.collector.PerfUnitCollector;
import dev.techh.configuration.ConfigurationLoader;
import dev.techh.configuration.data.Configuration;
import dev.techh.transformer.PerfUnitTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;

public class PerfUnit {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void premain(String arguments, Instrumentation instrumentation) {

        if (arguments == null) {
            LOG.error("Please specify config path. -javaagent:perfunit.jar=<CONFIG_PATH>");
            System.exit(-1);
        }

        LOG.info("Starting PerfUnit agent with config {}", arguments);
        Configuration configuration = ConfigurationLoader.load(arguments);
        LOG.info("Loaded config {}", configuration);
        PerfUnitCollector.create(configuration);

        instrumentation.addTransformer(new PerfUnitTransformer(configuration));
    }

}
