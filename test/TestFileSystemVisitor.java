import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.*;

public class TestFileSystemVisitor {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Before
    public void createTestData() throws IOException {
        String tempPath = tempFolder.getRoot().toString();
        System.out.format("created temp folder: %s", tempPath);
        tempFolder.newFile("empty.txt");

        File f1 = tempFolder.newFolder("f1");
        fillFileToSize(new File(f1, "1Mb.txt"), 1024);

        File f2 = tempFolder.newFolder("f2");
        fillFileToSize(new File(f2, "2Mb.txt"), 2048);

        File f3 = tempFolder.newFolder("f3");
        fillFileToSize(new File(f3, "3Mb.txt"), 3072);

        File f4 = new File(f3, "f4");
        f4.mkdir();
        fillFileToSize(new File(f4, "4Mb.txt"), 4096);


    }

    private void fillFileToSize(File file,int size) throws IOException {
        FileOutputStream s = new FileOutputStream(file);
        byte[] buf = new byte[size*1024];
        s.write(buf);
        s.flush();
        s.close();
    }

    @Test
    public void testAFileExists() {
        String tmpPath = tempFolder.getRoot().toString();
        assertTrue(new File(tmpPath + File.separator + "empty.txt").exists());
    }

    @Test
    public void testShouldShowDepthLevelOfOne() {
        Path path = tempFolder.getRoot().toPath();
        FileSystemVisitor visitor = new FileSystemVisitor(path, new JProgressBar());
        try {
            Files.walkFileTree(path, visitor);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals(4, visitor.getFoldersSizes().size());
        assertFalse(visitor.getFoldersSizes().containsKey("f4"));
    }

    @Test
    public void testVisitorWalkTree() {
        Path path = tempFolder.getRoot().toPath();
        FileSystemVisitor visitor = new FileSystemVisitor(path, new JProgressBar());
        try {
            Files.walkFileTree(path, visitor);
    } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Map<String,Long> foldersSizes = visitor.getFoldersSizes();
        System.out.println(foldersSizes);
        assertEquals((Object) 0L, foldersSizes.get("empty.txt"));
        assertEquals((Object) 1048576L, foldersSizes.get("f1"));
        assertEquals((Object) 2097152L, foldersSizes.get("f2"));
        assertEquals((Object) 7340032L, foldersSizes.get("f3"));
    }


}