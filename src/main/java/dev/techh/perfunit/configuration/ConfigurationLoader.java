package dev.techh.perfunit.configuration;

import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.exception.ConfigurationException;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;

@Factory
public class ConfigurationLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Singleton
    public Configuration load(@Property(name = "perfunit.config") String file) {
        LOG.info("Loading config {}", file);

        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        Yaml yaml = new Yaml(representer);

        try {
            return yaml.loadAs(new FileReader(file), Configuration.class);
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(e);
        }
    }

}
