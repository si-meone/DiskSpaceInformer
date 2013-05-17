package dsi;

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
import static org.fest.swing.timing.Pause.pause;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;



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
        fillFileToSize(new File(f2, "2Mb.txt"), 2048);

        File f3 = tempFolder.newFolder("f3");
        fillFileToSize(new File(f3, "3Mb.txt"), 3072);

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
        JTextComponentFixture log = new JTextComponentFixture(robot, "textArea");
        assertFalse("error found 1 MB in log window", log.text().contains("1 MB"));
        //tree.clickRow(2);
        String f1 = tempFolder.getRoot().getName() + "/" + "f1";
        JTreeFixture jTreeFixture = tree.clickPath(f1);
        new JButtonFixture(robot, "Check Space").click();
        pause(100);
        assertThat(jTreeFixture.valueAt(f1), is("f1"));
        assertTrue("error could not find 1 MB in log window", log.text().contains("1 MB"));
    }

  @Test
    public void testCheckTreeFolderSizeWithANumberOfFiles() {
        JTreeFixture tree = new JTreeFixture(robot, "tree");
        JTextComponentFixture log = new JTextComponentFixture(robot, "textArea");
        //tree.clickRow(2);
        String root = tempFolder.getRoot().getName();
        JTreeFixture jTreeFixture = tree.clickPath(root);
        new JButtonFixture(robot, "Check Space").click();
        pause(100);
        assertTrue("error could not find 10 MB in log window", log.text().contains("10 MB"));
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
