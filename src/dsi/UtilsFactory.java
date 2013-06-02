package dsi;

import com.ziclix.python.sql.util.PyArgParser;
import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class UtilsFactory {

    public UtilsFactory() {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("from Utils import Utils");
        utilsClass = interpreter.get("Utils");
    }

    public UtilsType create(String path) {
        PyObject utilsObj = utilsClass.__call__(new PyString(path));
        return (UtilsType)utilsObj.__tojava__(UtilsType.class);
    }

    private PyObject utilsClass;
}
