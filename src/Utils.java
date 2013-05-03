import javax.swing.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;

public final class Utils {

    static private final String newline = "\n";

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String prettyPrint(String root, long totalSpace, long usedSpace, long freeSpace ) {
        StringBuffer sb = new StringBuffer();
        String title = "Checking: [ " + root + " ]";
        String underline = String.format(String.format("%%0%dd", title.length()), 0).replace("0", "=");

        sb.append(underline + "\n" + title + "\n" + underline);
        sb.append(String.format("\nTotal Space is: [%s]\nUsed space is: [%s] \nFree space is: [%s] \n\n",
                readableFileSize(totalSpace),
                readableFileSize(usedSpace),
                readableFileSize(freeSpace))
        );
        return sb.toString();
    }

    public static String prettyPrint(File file, long total, Map<String, Long> sortedFileFolderSizes) {
        StringBuffer sb = new StringBuffer();
        String title = file.getName() + ": [" + readableFileSize(total) + "]";
        String underline = String.format(String.format("%%0%dd", title.length()), 0).replace("0", "=");
        sb.append(underline + newline);
        sb.append(title + newline);
        sb.append(underline + newline);
        for (Map.Entry<String, Long> entry : sortedFileFolderSizes.entrySet()) {
            sb.append("[ " + readableFileSize(entry.getValue()) + " ]");
            sb.append(" --> " + entry.getKey() + "\n");
        }
        sb.append(newline);
        return sb.toString();
    }

    public static String prettyPrint(File file, long total) {
        return String.format("%s: [%s]\n", file.getName(), readableFileSize(total));
    }

    public static String prettyPrint(File file) {
        return String.format("%s: [%s]\n", file.getName(), readableFileSize(file.length()));
    }

    public static String printInstructions() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Space usage: right click tree item(s) & Check Space" + newline);
        builder.append("- Alternative: select tree item(s) & click [Check Space]" + newline + newline);
        return builder.toString();
    }


}

