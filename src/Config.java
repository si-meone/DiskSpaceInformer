import java.nio.file.Path;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;


public class Config {
    private final String configFilePrefix;
    private ResourceBundle config;

    public Config(String resourceBundle) throws MissingResourceException{
        this.configFilePrefix = resourceBundle;
        this.config  = ResourceBundle.getBundle(resourceBundle);
    }

    public String[] getItems(String key) throws MissingResourceException{
        String[] stringArray = new String[0];
        try{
            stringArray = config.getString(key).split(",");
        }catch (MissingResourceException e){
             System.out.println(e);
             throw new MissingResourceException("Missing" , configFilePrefix, key );
        }
       return stringArray;

    }

    @Override
    public String toString() {
        return "Config{" +
                "config=" + config +
                '}';
    }
}
