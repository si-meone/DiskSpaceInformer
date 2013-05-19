package dsi;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public final class TextFormatter {

    static private final String newline = "\n";
    static private final String tab = "\t";
    static private final String doubleSpace = "  ";
    static private final String space = " ";


    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String format(String root, long totalSpace, long usedSpace, long freeSpace) {
        StringBuffer sb = new StringBuffer();
        long freeSpacePercent = Math.round(Float.valueOf(freeSpace) / Float.valueOf(totalSpace) * 100.0);
        String title = "Checked: [ " + root + " ]  " + "Free:" + " [ " + freeSpacePercent + "% ]";
        String underline = String.format(String.format("%%0%dd", title.length()/2), 0).replace("0", "─");

        sb.append(underline + newline + title + newline + underline);
        sb.append(String.format("\nTotal Space is: [ %s ]\nUsed space is: [ %s ] \nFree space is: [ %s ]\n\n",
                readableFileSize(totalSpace),
                readableFileSize(usedSpace),
                readableFileSize(freeSpace))
        );
        return sb.toString();
    }

    public String format(File file, long total) {
        return String.format("%s: [ %s ]\n", file.getName(), readableFileSize(total));
    }

    public  String format(File file) {
        return String.format("%s: [ %s ]\n", file.getName(), readableFileSize(file.length()));
    }

}

