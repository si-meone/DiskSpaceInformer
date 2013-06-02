package dsi;


import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

public class Utils {
    private UtilsType utilsType;
    private Float free_space = 0.0f;
    private String folder = "";

    public Utils() {
//        String path1 = Paths.get("").toAbsolutePath().toString() + File.separator +
//                "src"+ File.separator + "dsi";
//        String path2 = Paths.get("").toAbsolutePath().toString() + File.separator +
//                "dsi";
//        Properties props = new Properties();
//        if (System.getProperty("os.name").startsWith("Win")){
//            props.setProperty("python.path", path1 + ";" + path2);
//        } else{
//            props.setProperty("python.path", path1 + ":" + path2);
//        }
//        log.info("setting python.path to: " + path1);
//        log.info("setting python.path to: " + path2);

//        PythonInterpreter.initialize(System.getProperties(), props,
//                new String[] {""});

        PySystemState pySysState = new PySystemState();
        log.info("Jython sys state initialized. sys.path: " + pySysState.path);

        UtilsFactory factory = new UtilsFactory();
        if (System.getProperty("os.name").startsWith("Win")){
            folder = "c:\\" ;
        }else{
            folder = "/" ;
        }
        utilsType = factory.create(folder);

    }

    private static Logger log = Logger.getLogger(Utils.class.getName());


    public String[][] getFreeSpace() {
        String[][] s = new String[2][2];
        s[0][0] = folder;
        s[0][1] = utilsType.get_free_space();
        return s;
    }

    public static void main(String[] args) {
        new Utils();

    }


}
