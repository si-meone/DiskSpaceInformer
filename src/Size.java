import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Size {


    public static void main(String[] args) throws Exception {
        //if (args.length != 1) System.out.println("Use: java Size <directory>");
            Path root = Paths.get(System.getProperty("user.home"));
       CalculateFileSizeVisitor visitor = new CalculateFileSizeVisitor();
       Files.walkFileTree(root, visitor);

        System.out.format("The given directory contains %d files spred in %d directories, occupying %.4f MB in disk", visitor.numFiles, visitor.numDirs, visitor.sizeSum / (1024.0 * 1024));
    }
}


class CalculateFileSizeVisitor implements FileVisitor<Path> {
    int numFiles = 0;
    int numDirs = 0;
    long sizeSum = 0;

    public long getSizeSum() {
        return sizeSum;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        //System.out.println("[D] " + dir);
        //numDirs++;
        return FileVisitResult.CONTINUE;
     }
    @Override

    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        //System.out.println("[F]\t " + file);
        sizeSum += attrs.size();
        //numFiles++;
        return FileVisitResult.CONTINUE;
        }
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.err.println("It was not possible to analyze the file: " + file + " caused by: " + exc);
        return FileVisitResult.TERMINATE;
        }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
        }
}