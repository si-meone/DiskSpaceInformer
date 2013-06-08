package dsi;

import java.text.DecimalFormat;

public class HumanReadableFileSize implements Comparable{
    private Long size;
    private String humanReadableSize;

    public HumanReadableFileSize(Float size) {
        this(size.longValue());
    }

    public HumanReadableFileSize(Long size) {
        this.size = size;
        this.humanReadableSize = readableFileSize(size);
    }


    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    @Override
    public int compareTo(Object o) {
        HumanReadableFileSize humanReadableFileSize = (HumanReadableFileSize) o;

        if (size < humanReadableFileSize.size ){
            return -1;
        } else if (size > humanReadableFileSize.size){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public String toString() {
        return humanReadableSize;
    }
}
