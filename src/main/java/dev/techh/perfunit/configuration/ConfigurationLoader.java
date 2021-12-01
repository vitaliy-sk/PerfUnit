package dev.techh.perfunit.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.exception.ConfigurationException;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Factory
public class ConfigurationLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Singleton
    public Configuration load(@Property(name = "perfunit.config") String file) {
        LOG.info("Loading config {}", file);

        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        try {
            FileReader fileReader = new FileReader(file);
            return om.readValue(fileReader, Configuration.class);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

}
