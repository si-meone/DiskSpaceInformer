import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;

public class Size {


    public static void main(String[] args) throws Exception {
       if (args.length != 1){ System.out.println("Use: java Size <directory>");};
       Path root = Paths.get(System.getProperty("user.home"));
       CalculateFileSizeVisitor visitor = new CalculateFileSizeVisitor(root);
       Files.walkFileTree(root, visitor);
    }
}

class CalculateFileSizeVisitor implements FileVisitor<Path> {

    private JProgressBar progressBar;
    private long grandTotal = 0;
    private long dirTotal = 0;
    //private Map<String, Long> foldersSizes = new TreeMap(new ValueComparator(new TreeMap<String, Long>()));
    private Map<String, Long> foldersSizes = new LinkedHashMap<String, Long>();

    private Path path;
    public Map<String, Long> getFoldersSizes() {
        return foldersSizes;
    }

    CalculateFileSizeVisitor(Path path) {
        this.path = path;
    }

    CalculateFileSizeVisitor(Path path, JProgressBar progressBar) {
        this.path = path;
        this.progressBar = progressBar;
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
        if(file.equals(path) || !file.getParent().equals(path)){ //not on the 1 st level
            dirTotal += attrs.size();
            return FileVisitResult.CONTINUE;
        }else{ // on first level
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
        if(dir.equals(path) || !dir.getParent().equals(path)){
            return FileVisitResult.CONTINUE;
        }

        foldersSizes.put(dir.getFileName().toString(), dirTotal);
        grandTotal += dirTotal;
        dirTotal = 0L; //reset
       //System.out.format("foldersSizes: %s",foldersSizes);
       return FileVisitResult.CONTINUE;
        }
}