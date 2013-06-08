package dsi;


import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.util.Properties;
import java.util.logging.Logger;

public class Utils {
    private UtilsType utilsType;
    private Float free_space = 0.0f;
    private String folder = "";

    public Utils() {
        String path1 = "__pyclasspath__/Lib";
        String path2 = "__pyclasspath__/dsi";
        Properties props = new Properties();
//        props.setProperty("python.path", path1);
//        props.setProperty("python.path", path2);
//        PythonInterpreter.initialize(System.getProperties(), props,
//              new String[] {""});
        if (System.getProperty("os.name").startsWith("Win")){
            props.setProperty("python.path", path1 + ";" + path2);
        } else{
            props.setProperty("python.path", path1 + ":" + path2);
        }
//        log.info("setting python.path to: " + path1);
//        log.info("setting python.path to: " + path2);

        PythonInterpreter.initialize(System.getProperties(), props,
                new String[] {""});

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
        return utilsType.get_free_space();
//        String[][] s = new String[2][2];
//        s[0][0] = folder;
//        s[0][1] = String.valueOf(utilsType.get_free_space());
//        return s;
    }

    public float get_dir_size(String path){
        return utilsType.get_dir_size(path);
    }

    public static void main(String[] args) {
        String[][] freeSpace = new Utils().getFreeSpace();
        System.out.println(freeSpace[0][0] + " " + freeSpace[0][1]);

    }


}
