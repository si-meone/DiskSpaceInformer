import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import static java.lang.Class.*;
import static java.util.ResourceBundle.getBundle;


public class Config {
    private final String configFilePrefix;
    private Properties props;

    public Config(String property) throws MissingResourceException{
        this.configFilePrefix = property;
        this.loadParams(property);
    }

    public String[] getItems(String key) throws MissingResourceException{
        String properties = props.getProperty(key, "");
        System.out.println("loading properties: " + properties);
        if (properties.equals("")){
            return new String[0];
        }else{
            return properties.split(",");
        }
//        new Properties()
//        String[] stringArray = new String[0];
//        try{
//            System.out.println("config.getString(key): " + config.getString(key));
//            stringArray = config.getString(key).split(",");
//        }catch (MissingResourceException e){
//             System.out.println(e);
//             throw new MissingResourceException("Missing" , configFilePrefix, key );
//        }
//       return stringArray;

    }

    public void loadParams(String properties) {
        props = new Properties();
        InputStream is = null;

        // First try loading from the current directory
        try {
            File f = new File(properties);
            is = new FileInputStream( f );
        }
        catch ( Exception e ) { is = null; }

        try {
            if ( is == null ) {
                // Try loading from classpath
                is = getClass().getResourceAsStream(properties);
            }

            // Try loading properties from the file (if found)
            props.load( is );
        }
        catch ( Exception e ) { }

//        serverAddr = props.getProperty("ServerAddress", "192.168.0.1");
//        serverPort = new Integer(props.getProperty("ServerPort", "8080"));
//        threadCnt  = new Integer(props.getProperty("ThreadCount", "5"));
    }

    @Override
    public String toString() {
        return "Config{" +
                "config=" + props +
                '}';
    }
}
