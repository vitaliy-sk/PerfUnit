package dev.techh.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.techh.configuration.data.Configuration;
import dev.techh.exception.ConfigurationException;

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
