import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

class IgnoreFilter implements FileFilter {

    public IgnoreFilter() {
    }

    public boolean accept(File file) {
        if (isVirtualFileSystem(file) || isSymlink(file)) {
            return false;
        }
        return true;
    }


    public boolean isSymlink(File file) {
        File canon = null;
        boolean isLink = false;
        try {
            if (file.getParent() == null) {
                canon = file;
            } else {
                File canonDir = file.getParentFile().getCanonicalFile();
                canon = new File(canonDir, file.getName());
                isLink = !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isLink;
    }

    /*
        Folders to avoid they give strange readings
     */
    private boolean isVirtualFileSystem(File file) {
        boolean isVfs = false;
        String absPath = file.getAbsolutePath();
        if (absPath.equalsIgnoreCase("/proc")) {
            isVfs = true;
        }
        if (absPath.equalsIgnoreCase("/dev")) {
            isVfs = true;
        }
        return isVfs;  //To change body of created methods use File | Settings | File Templates.
    }

}