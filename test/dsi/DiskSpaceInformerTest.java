package dsi;

import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.*;
import org.junit.*;


import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.finder.FrameFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.launcher.ApplicationLauncher;
import org.junit.rules.TemporaryFolder;

import javax.swing.JButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static org.fest.swing.data.TableCell.row;
import static org.fest.swing.timing.Pause.pause;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;


public class DiskSpaceInformerTest{
    public  FrameFixture window;
    private Robot robot;
    ApplicationLauncher app;
    private final int delay = 50;

    public DiskSpaceInformerTest() {
    }

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Before
    public void setUp() throws Exception {
        String tempPath = tempFolder.getRoot().toString();
        //System.out.format("created temp folder: %s", tempPath);
        tempFolder.newFile("empty.txt");

        File f1 = tempFolder.newFolder("f1");
        fillFileToSize(new File(f1, "1Mb.txt"), 1024);

        File f2 = tempFolder.newFolder("f2");
        fillFileToSize(new File(f2, "2Mb.txt"), 3072);

        File f3 = tempFolder.newFolder("f3");
        fillFileToSize(new File(f3, "3Mb.txt"), 2048);

        File f4 = new File(f3, "f4");
        f4.mkdir();
        fillFileToSize(new File(f4, "4Mb.txt"), 4096);

        app = ApplicationLauncher.application(DiskSpaceInformer.class);
        app.withArgs(tempPath);
        app.start();

        robot = BasicRobot.robotWithCurrentAwtHierarchy();
        robot.settings().delayBetweenEvents(delay);

        FrameFinder ff = WindowFinder.findFrame("DiskSpaceInformer");

        // Find the main window
        window = ff.using(robot);
    }


    /**
     * Check if the adapter returns the correct window
     */
    @Test
    public void testCheckSpaceButtonWindow() {
        JButtonFixture button = window.button(
                new GenericTypeMatcher<JButton>(JButton.class) {
                    @Override protected boolean isMatching(JButton button) {
                        return "Check Space".equals(button.getText());
                    }

                });
        assertThat(button.text(), is("Check Space"));
    }


    @Test
    public void testCheckTreeFolderWithOneMbFile() {
        JTreeFixture tree = new JTreeFixture(robot, "tree");
//        JTextComponentFixture log = new JTextComponentFixture(robot, "textArea");
        JTableFixture table = new JTableFixture(robot, "table");
        //assertFalse("error found 1 MB in log window", log.text().contains("1 MB"));
        //tree.clickRow(2);
        String f1 = tempFolder.getRoot().getName() + "/" + "f1";
        tree.clickPath(f1);
        new JButtonFixture(robot, "Check Space").click();
        pause(100);
        assertThat(table.valueAt(row(0).column(0)), is("1Mb.txt"));
        assertThat(table.valueAt(row(0).column(1)), is("1 MB"));
    }

    @Ignore
    public void testCheckTreeFolderResultSorting() {
        JTreeFixture tree = new JTreeFixture(robot, "tree");
        JTextComponentFixture log = new JTextComponentFixture(robot, "textArea");
        String root = tempFolder.getRoot().getName();
        tree.clickPath(root);
        new JButtonFixture(robot, "Check Space").click();
        pause(100);
        assertThat(log.text(), containsString("\\\n" +
                "\\__  [ 6 MB ]   f3 \n" +
                "\\__  [ 3 MB ]   f2 \n" +
                "\\__  [ 1 MB ]   f1 \n" +
                "\\__  [ 0 ]   empty.txt "));
    }

    //as well as to clean it up after the test:

    @After
    public void tearDown()
    {
        window.cleanUp();
    }

    private void fillFileToSize(File file,int size) throws IOException {
        FileOutputStream s = new FileOutputStream(file);
        byte[] buf = new byte[size*1024];
        s.write(buf);
        s.flush();
        s.close();
    }

}
