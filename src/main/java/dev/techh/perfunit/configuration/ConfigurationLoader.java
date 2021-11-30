package dev.techh.perfunit.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.exception.ConfigurationException;

import java.io.FileReader;
import java.io.IOException;

public class ConfigurationLoader {

    public static Configuration load(String file) {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        try {
            FileReader fileReader = new FileReader(file);
            return om.readValue(fileReader, Configuration.class);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

}
