import java.io.File;
import java.util.Arrays;

public class TreeFile extends java.io.File {
    private final File file;

    public TreeFile(java.io.File file) {
        super(file.getAbsolutePath());
        this.file = file;
    }

    public TreeFile(String file) {
        super(file);
        this.file = new File(file);
    }

    @Override
    public String toString() {
        if (Arrays.asList(File.listRoots()).contains(file)) {
            return getPath();
        } else {
            return getName();
        }
    }
}