package dev.techh;

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

        LOG.info("Starting PerfUnit agent with config {}", arguments);
        Configuration configuration = ConfigurationLoader.load(arguments);
        LOG.info("Loaded config {}", configuration);

        instrumentation.addTransformer(new PerfUnitTransformer(configuration));
    }

}
