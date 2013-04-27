import java.io.File;
import java.io.FileFilter;

/**
* Created with IntelliJ IDEA.
* User: snasrallah
* Date: 27/04/2013
* Time: 13:23
* To change this template use File | Settings | File Templates.
*/
class DirectoryFilter implements FileFilter {
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }
        return false;
    }
}
