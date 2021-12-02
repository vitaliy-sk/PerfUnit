package dev.techh.perfunit;

import dev.techh.perfunit.transformer.PerfUnitTransformer;
import dev.techh.perfunit.utils.ContextHolder;
import io.micronaut.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.util.Map;

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

        System.setProperty("micronaut.config.files", arguments);
        Map<String, Object> properties = Map.of("perfunit.config", arguments);
        ApplicationContext applicationContext = ApplicationContext.builder()
                .eagerInitSingletons(true)
                .properties(properties)
                .build().start();
        ContextHolder.setContext(applicationContext);

        PerfUnitTransformer transformer = applicationContext.getBean(PerfUnitTransformer.class);
        LOG.info("Starting transformations...");
        instrumentation.addTransformer(transformer);
    }

}
