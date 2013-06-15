package dsi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Config {
    private final String configFilePrefix;
    private Properties props;
    static public final String appName = "Disk Space Informer ";
    static public final String version = "version 0.1y";
    private static Logger log = Logger.getLogger(Config.class.getName());


    public Config(String property) throws MissingResourceException {
        log.log(Level.CONFIG, "loading property file " + property);
        this.configFilePrefix = property;
        this.loadParams(property);
    }

    public String[] getItems(String key) throws MissingResourceException {
        String properties = props.getProperty(key, "");
        log.log(Level.CONFIG, "loading properties: " + properties);
        if (properties.equals("")) {
            return new String[0];
        } else {
            return properties.split(",");
        }
    }

    public void loadParams(String properties) {
        log.log(Level.CONFIG, String.format("Loading property file: %s", properties));
        props = new Properties();
        InputStream is = null;
        StringBuilder errors = new StringBuilder();
        log.log(Level.CONFIG, "First try loading from the current directory");
        try {
            File f = new File(properties);
            is = new FileInputStream(f);
        } catch (Exception e) {
            errors.append(e);
            is = null;
            log.log(Level.CONFIG, properties + " not found in current directory");
        }

        try {
            if (is == null) {
                log.log(Level.CONFIG, "Try loading from classpath");
                is = getClass().getResourceAsStream(properties);
            }
        } catch (Exception e) {
            errors.append(e);
            log.log(Level.CONFIG, properties + "not found in class path");
        }
        try{
            props.load(is);
        }catch (Exception e){
            log.log(Level.WARNING, "Error loading " + properties + ": " + errors.toString(), e );
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "configFilePrefix='" + configFilePrefix + '\'' +
                ", props=" + props +
                '}';
    }
}
