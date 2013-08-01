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
        if (System.getProperty("os.name").startsWith("Win")){
            props.setProperty("python.path", path1 + ";" + path2);
        } else{
            props.setProperty("python.path", path1 + ":" + path2);
        }
        //log.info("setting python.path to: " + path1);
        //log.info("setting python.path to: " + path2);

        PythonInterpreter.initialize(System.getProperties(), props,
                new String[] {""});

        //PySystemState pySysState = new PySystemState();
        //log.info("Jython sys state initialized. sys.path: " + pySysState.path);

        UtilsFactory factory = new UtilsFactory();
        if (System.getProperty("os.name").startsWith("Win")){
            folder = "c:\\" ;
        }else{
            folder = "/" ;
        }
        utilsType = factory.create(folder);

    }

    private static Logger log = Logger.getLogger(Utils.class.getName());


    public String[][] getInfo() {
        String[][]array1 =  utilsType.get_free_space();
        String[] items = new Config("config.properties").getItems("folders.to.ignore");

        int row = 0;
        String[][]array2;
        if (items.length >1){
            array2 = new String[items.length +6][2];
            for (String item : items){
                array2[row][0] = "ignored on scan";
                array2[row][1] = item;
                row++;
            }
        } else{
            array2 = new String[6][2];
        }

        row++;
        array2[row][0] = "os.name";
        array2[row][1] = System.getProperty("os.name");
        row++;
        array2[row][0] = "os.version";
        array2[row][1] = System.getProperty("os.version");
        row++;
        array2[row][0] = "java.version";
        array2[row][1] = System.getProperty("java.version");
        row++;
        array2[row][0] = "jvm.freeMemory";
        array2[row][1] = new HumanReadableFileSize(Runtime.getRuntime().freeMemory()).toString();
        row++;
        array2[row][0] = Config.appName;
        array2[row][1] = Config.version;

        String[][]array3 = new String[array1.length + array2.length][array1.length + array2.length]; //accumulation of both files.....

        System.arraycopy(array1, 0, array3, 0, array1.length);
        System.arraycopy(array2, 0, array3, array1.length, array2.length);

        return array3;
    }

    public float get_dir_size(String path){
        return utilsType.get_dir_size(path);
    }

    public String get_errors(){
        return utilsType.get_errors();
    }

    public static void main(String[] args) {
        String[][] freeSpace = new Utils().getInfo();
        System.out.println(freeSpace[0][0] + " " + freeSpace[0][1]);

    }


}
