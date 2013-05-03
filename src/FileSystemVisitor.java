import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileSystemVisitor implements FileVisitor<Path> {

    private JProgressBar progressBar;
    private long grandTotal = 0;
    private long dirTotal = 0;
    private Map<String, Long> foldersSizes = new LinkedHashMap<String, Long>();

    private Path path;

    public Map<String, Long> getFoldersSizes() {
        return foldersSizes;
    }

    FileSystemVisitor(Path path, JProgressBar progressBar) {
        this(path);
        this.progressBar = progressBar;
        this.progressBar.setString("Determining files to scan");
        this.progressBar.setStringPainted(true);
        this.progressBar.setVisible(true);
        this.progressBar.setIndeterminate(true);
    }

    FileSystemVisitor(Path path) {
        this.path = path;
    }

    public long getGrandTotal() {
        return grandTotal;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        //System.out.println("[D]\t " + dir);
        progressBar.setString(dir.getFileName().toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        //System.out.println("[F]\t " + file);
        if (file.equals(path) || !file.getParent().equals(path)) { //not on the 1 st level
            dirTotal += attrs.size();
            return FileVisitResult.CONTINUE;
        } else { // on first level
            grandTotal += attrs.size();
            foldersSizes.put(file.getFileName().toString(), attrs.size());
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.err.println("It was not possible to analyze the file: " + file + " caused by: " + exc);
        return FileVisitResult.TERMINATE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (dir.equals(path) || !dir.getParent().equals(path)) {
            return FileVisitResult.CONTINUE;
        }

        foldersSizes.put(dir.getFileName().toString(), dirTotal);
        grandTotal += dirTotal;
        dirTotal = 0L; //reset
        //System.out.format("foldersSizes: %s",foldersSizes);
        return FileVisitResult.CONTINUE;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Use: java Size <directory>");
        }
        Path root = Paths.get(System.getProperty("user.home"));
        FileSystemVisitor visitor = new FileSystemVisitor(root);
        Files.walkFileTree(root, visitor);
    }
}