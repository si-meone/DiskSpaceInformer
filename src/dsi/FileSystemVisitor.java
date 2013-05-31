package dsi;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class FileSystemVisitor implements FileVisitor<Path> {

    private JProgressBar progressBar;
    private long grandTotal = 0;
    private long dirTotal = 0;

    private Map<String, HumanReadableFileSize> foldersSizes = new LinkedHashMap<String, HumanReadableFileSize>();
    private List<Path> pathsToIgnore;

    private static Logger log = Logger.getLogger(FileSystemVisitor.class.getName());

    private StringBuilder errors = new StringBuilder();

    private PythonInterpreter interp = new PythonInterpreter();
    private PyDictionary folderSizesDict = new PyDictionary();

    private Path path;

    public Map<String, HumanReadableFileSize> getFoldersSizes() {
        return foldersSizes;
    }

    FileSystemVisitor(Path path, List pathsToIgnore, JProgressBar progressBar) {
        this(path);
        this.pathsToIgnore = pathsToIgnore;
        this.progressBar = progressBar;
        this.progressBar.setString("Determining files to scan");
        this.progressBar.setStringPainted(true);
        this.progressBar.setVisible(true);
        interp.set("folderSizesDict", folderSizesDict);
    }

    FileSystemVisitor(Path path) {
        this.path = path;
    }

    public long getGrandTotal() {
        return grandTotal;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        if (Thread.interrupted()) {
            return FileVisitResult.TERMINATE;
        }
        log.log(Level.FINE, "[D]\t " + dir);
        progressBar.setString(dir.toString());
        if (pathsToIgnore.contains(dir)){
            foldersSizes.put(dir.toString(), new HumanReadableFileSize(0L));
//            interp.exec(String.format("folderSizesDict[%s] = %s", dir.toString(), 0L));
            errors.append("EXCLUDING: " + dir.toString() + "\n");
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        log.log(Level.FINE, "[F]\t " + file);
        if (pathsToIgnore.contains(file)){
            foldersSizes.put(file.toString(), new HumanReadableFileSize(0L));
//            interp.exec(String.format("folderSizesDict[%s] = %s", file.toString(), 0L));
            errors.append("EXCLUDING: " + file.toString() + "\n" );
            return FileVisitResult.CONTINUE;
        }
        if (file.equals(path) || !file.getParent().equals(path)) { //not on the 1 st level
            dirTotal += attrs.size();
            return FileVisitResult.CONTINUE;
        } else { // on first level
            grandTotal += attrs.size();
            foldersSizes.put(file.getFileName().toString(), new HumanReadableFileSize(attrs.size()));
               //folderSizesDict.update(new PyObject(file.getFileName().toString()), new PyObject(new HumanReadableFileSize(attrs.size())));
               interp.exec("folderSizesDict[\"" + file.getFileName().toString() + "\"] = " + "\"" + new HumanReadableFileSize(attrs.size()) + "\"");
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
        if (dir.equals(path) || !dir.getParent().equals(path)) { // start or first level folder
            return FileVisitResult.CONTINUE;
        }
        // first level folder
        foldersSizes.put(dir.getFileName().toString(), new HumanReadableFileSize(dirTotal));
//        interp.exec(String.format("folderSizesDict[%s] = %s",dir.getFileName().toString(), dirTotal));
        grandTotal += dirTotal;

        dirTotal = 0L; //reset
        log.log(Level.FINE, "foldersSizes: %s",foldersSizes);
        return FileVisitResult.CONTINUE;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        if (args.length != 1) {
            System.out.println("Use: java Size <directory>");
        }
        Path root = Paths.get(System.getProperty("user.home"));
//        Path root = Paths.get(new File("/").toURI());
        List<String> foldersToIgnore  = new ArrayList<String>();
        foldersToIgnore.add("");
        FileSystemVisitor visitor = new FileSystemVisitor(root, foldersToIgnore, new JProgressBar());
        Files.walkFileTree(root, visitor);
        System.out.println(visitor.getFoldersSizes());

        System.out.println("---python---");
        visitor.interp.exec("print folderSizesDict");
        System.out.println("---python---");

        long endTime = System.currentTimeMillis();
        log.info("Scan of" + root  + " took " + (endTime - startTime) + " milliseconds");
    }
}
