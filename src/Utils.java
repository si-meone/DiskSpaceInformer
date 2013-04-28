import javax.swing.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;

public final class Utils {

    private static String OS = null;
    static private final String newline = "\n";

    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void prettyPrint(File file, long total, Map<String, Long> sortedFileFolderSizes, JTextArea log) {
        String title = file.getName() + ": [" + readableFileSize(total) + "]";
        String underline = String.format(String.format("%%0%dd", title.length()), 0).replace("0", "=");
        log.append(underline + newline);
        log.append(title + newline);
        log.append(underline + newline);
        for (Map.Entry<String, Long> entry : sortedFileFolderSizes.entrySet()) {
            log.append("[ " + readableFileSize(entry.getValue()) + " ]");
            log.append(" --> " + entry.getKey() + "\n");
        }
        log.append(newline);
    }

    public static void prettyPrint(File file, long total, JTextArea log) {
        String s = String.format("%s: [%s]\n", file.getName(), readableFileSize(total));
        log.append(s);
    }

    public static void prettyPrint(File file, JTextArea log) {
        String s = String.format("%s: [%s]\n", file.getName(), readableFileSize(file.length()));
        log.append(s);
    }

    public static String checkSpaceAvailable() {
        StringBuffer sb = new StringBuffer();
        File[] roots = File.listRoots();
        for (File root : roots) {
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;

            String title = "Checking: [ " + root + " ]";
            String underline = String.format(String.format("%%0%dd", title.length()), 0).replace("0", "=");

            sb.append(underline + "\n" + title + "\n" + underline);
            sb.append(String.format("\nTotal Space is: [%s]\nUsed space is: [%s] \nFree space is: [%s] \n\n",
                    Utils.readableFileSize(totalSpace),
                    Utils.readableFileSize(usedSpace),
                    Utils.readableFileSize(freeSpace))
            );
        }
        return sb.toString();
    }

    public static String printInstructions() {
        StringBuilder builder = new StringBuilder();

        builder.append("- Select drive or folder: click [Choose Folder]" + newline + newline);
        builder.append("- Space usage: right click tree item & Check Space" + newline);
        builder.append("- Alternative: select tree item & click [Check Space]" + newline + newline);
        builder.append("- Multiple items: select multiple items right click" + newline);
        builder.append("- Alternative: select multiple items [Check Space]" + newline + newline);
        builder.append("- All drives: overview of system [Storage Info]" + newline + newline);
        builder.append("- Clear screen: overview of system [Storage Info]" + newline);
        return builder.toString();
    }


}

