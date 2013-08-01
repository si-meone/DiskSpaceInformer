package dsi;

import dsi.FileSystemTreeModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileSystemTreeModelTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void createTestData() throws IOException {
        String tempPath = tempFolder.getRoot().toString();
        //System.out.format("created temp folder: %s", tempPath);
        tempFolder.newFile("empty.txt");

        File f1 = tempFolder.newFolder("f1");
        new File(f1, "a.txt");

        File f2 = tempFolder.newFolder("f2");
        new File(f2, "b.txt");
        new File(f2, "c.txt");

        File f3 = new File(f2, "f3");
        f3.mkdir();


    }

    @Test
    public void testGetRoot() throws Exception {
        FileSystemTreeModel fileSystemTreeModel = new FileSystemTreeModel(tempFolder.getRoot().getPath());
        assertThat(fileSystemTreeModel.getRoot().toString(),is(tempFolder.getRoot().getName()));
        assertThat(fileSystemTreeModel.getChildCount(fileSystemTreeModel.getRoot()),is(3));
    }
}
