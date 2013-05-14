package dsi;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: snasrallah
 * Date: 14/05/2013
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
public interface Formatter {

    public String readableFileSize(long size);

    public String format(String root, long totalSpace, long usedSpace, long freeSpace);

    public String format(File file, long total, Map<String, Long> sortedFileFolderSizes, String extraInfo);

    public  String format(File file, long total);

    public String format(File file);

}
