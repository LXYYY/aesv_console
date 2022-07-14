package ntu.aesv_console;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ConfigParser {
    private Map config;
    public ConfigParser(String configFile) {
        // parse json file
        // json file to string
        JSONObject json=null;
        try {
            InputStream is = new FileInputStream(configFile);
            JSONTokener tokener = new JSONTokener(is);
            json = new JSONObject(tokener);
            System.out.println(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // json to map
        config = json.toMap();
    }

    public Map getConfig() {
        return config;
    }
}
