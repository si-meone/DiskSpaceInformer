package dsi;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemVisitor implements FileVisitor<Path> {

    private JProgressBar progressBar;
    private long grandTotal = 0;
    private long dirTotal = 0;
    private Map<String, Long> foldersSizes = new LinkedHashMap<String, Long>();
    private List<Path> pathsToIgnore;
    private static Logger log = Logger.getLogger(FileSystemVisitor.class.getName());


    public String getErrors() {
        return errors.toString();
    }

    private StringBuilder errors = new StringBuilder();

    private Path path;

    public Map<String, Long> getFoldersSizes() {
        return foldersSizes;
    }

    FileSystemVisitor(Path path, List pathsToIgnore,  JProgressBar progressBar) {
        this(path);
        this.pathsToIgnore = pathsToIgnore;
        this.progressBar = progressBar;
        this.progressBar.setString("Determining files to scan");
        this.progressBar.setStringPainted(true);
        this.progressBar.setVisible(true);
    }

    FileSystemVisitor(Path path) {
        this.path = path;
    }

    public long getGrandTotal() {
        return grandTotal;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        log.log(Level.FINE, "[D]\t " + dir);
        progressBar.setString(dir.toString());
        if (pathsToIgnore.contains(dir)){
            foldersSizes.put(dir.toString(), 0L);
            errors.append("EXCLUDING: " + dir.toString() + "\n" );
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        log.log(Level.FINE, "[F]\t " + file);
        if (pathsToIgnore.contains(file)){
            foldersSizes.put(file.toString(), 0L);
            errors.append("EXCLUDING: " + file.toString() + "\n" );
            return FileVisitResult.CONTINUE;
        }
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
        String error = "ERROR: " + file + " : " + exc + "\n";
        log.log(Level.WARNING, error);
        errors.append(error);
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (dir.equals(path) || !dir.getParent().equals(path)) {
            return FileVisitResult.CONTINUE;
        }

        foldersSizes.put(dir.getFileName().toString(), dirTotal);
        grandTotal += dirTotal;
        dirTotal = 0L; //reset
        log.log(Level.FINE, "foldersSizes: %s",foldersSizes);
        return FileVisitResult.CONTINUE;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Use: java Size <directory>");
        }
        Path root = Paths.get(System.getProperty("user.home"));
        List<String> foldersToIgnore  = new ArrayList<String>();
        foldersToIgnore.add("/proc");
        FileSystemVisitor visitor = new FileSystemVisitor(root, foldersToIgnore, new JProgressBar());
        Files.walkFileTree(root, visitor);
        System.out.println(visitor.getFoldersSizes());
    }
}