package ntu.aesv_console.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.Map;

public class MessageParser {

    public final MessageHeaders messageHeaders;

    public MessageParser(String messageHeaderFile) throws FileNotFoundException {
        this.messageHeaders = new MessageHeaders(messageHeaderFile);
    }

    public String getHeader(String message) {
        Map msgDef = (Map) this.messageHeaders.get(message);
        return String.valueOf(msgDef.get("header"));
    }

    public static class MessageHeaders {
        private final Map key2Header;

        public Object get(String key) {
            if (!key2Header.containsKey(key)) {
                throw new RuntimeException("No header for key: " + key);
            }
            return key2Header.get(key);
        }

        MessageHeaders(String messageFile) throws FileNotFoundException {

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            InputStream resource = new FileInputStream(messageFile);
            Map readValue = null;
            try {
                readValue = mapper.readValue(resource, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(readValue);

            key2Header = readValue;
        }
    }

}
