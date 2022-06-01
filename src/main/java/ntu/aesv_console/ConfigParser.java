package ntu.aesv_console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ConfigParser {
    private Map config;
    public ConfigParser(String configFile) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        InputStream resource = null;
        try {
            resource = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            config = mapper.readValue(resource, Map.class);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        System.out.println(config);
    }

    public Map getConfig() {
        return config;
    }
}
