package dsi;

import dsi.Config;
import dsi.FileSystemVisitor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class FileSystemVisitorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Before
    public void createTestData() throws IOException {
        String tempPath = tempFolder.getRoot().toString();
        //System.out.format("created temp folder: %s", tempPath);
        tempFolder.newFile("empty.txt");
        tempFolder.newFile("config_test.properties");

        File f1 = tempFolder.newFolder("f1");
        fillFileToSize(new File(f1, "1Mb.txt"), 1024);

        File f2 = tempFolder.newFolder("f2");
        fillFileToSize(new File(f2, "2Mb.txt"), 2048);

        File f3 = tempFolder.newFolder("f3");
        fillFileToSize(new File(f3, "3Mb.txt"), 3072);

        File f3_1 = new File(f3, "f3_1");
        f3_1.mkdir();
        fillFileToSize(new File(f3_1, "4Mb.txt"), 4096);

        File f3_2 = new File(f3, "f3_2");
        f3_2.mkdir();
        fillFileToSize(new File(f3_2, "4Mb.txt"), 1024);

        File f3_3 = new File(f3, "f3_3");
        f3_3.mkdir();
        fillFileToSize(new File(f3_3, "4Mb.txt"), 1024);


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
        FileSystemVisitor visitor = new FileSystemVisitor(path, new ArrayList(), new JProgressBar());
        try {
            Files.walkFileTree(path, visitor);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals(5, visitor.getFoldersSizes().size());
        assertFalse(visitor.getFoldersSizes().containsKey("f4"));
    }

    @Test
    public void testVisitorWalkTree() {
        Path path = tempFolder.getRoot().toPath();
        FileSystemVisitor visitor = new FileSystemVisitor(path, new ArrayList(), new JProgressBar());
        try {
            Files.walkFileTree(path, visitor);
    } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Map<String,Long> foldersSizes = visitor.getFoldersSizes();
        //System.out.println(foldersSizes);
        assertEquals((Object) 0L, foldersSizes.get("empty.txt"));
        assertEquals((Object) 1048576L, foldersSizes.get("f1"));
        assertEquals((Object) 2097152L, foldersSizes.get("f2"));
        assertEquals((Object) 9437184L, foldersSizes.get("f3"));
    }

    @Test
    public void testVisitorWalkTreeIgnoresAPathFromConfig() throws Exception {
        Path root = tempFolder.getRoot().toPath();
        String propertyName = "config_test.properties";
        String replace = root.toString().replace("\\", "/");
        File property = new File(replace + "/" + propertyName);

        String path1 = replace + "/" +  "f3" + "/" + "f3_2";
        String path2 = replace + "/"  + "f3" + "/" + "f3_3";
        PrintWriter out = new PrintWriter(property);
        out.println("folders.to.ignore=" + path1 + ","+  path2);
        out.close();

        addToClasspath(property.getParentFile());
        String[] paths  = new Config(replace + "/" + "config_test.properties").getItems("folders.to.ignore");

        List foldersToIgnore = new ArrayList<Path>();
        for (String path : paths){
            File file = new File(path);
            foldersToIgnore.add(file.toPath());
        }
        FileSystemVisitor visitor = new FileSystemVisitor(root, foldersToIgnore, new JProgressBar());
        try {
            Files.walkFileTree(root, visitor);
    } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Map<String,Long> foldersSizes = visitor.getFoldersSizes();
        assertEquals((Object) 7340032L, foldersSizes.get("f3"));
    }

    private static void addToClasspath(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

}