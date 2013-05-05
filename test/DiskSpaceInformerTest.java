import junit.extensions.TestSetup;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class DiskSpaceInformerTest {

    private JFrame testFrame;


    public JFrame getTestFrame(  ) {
        if (this.testFrame == null) {
            this.testFrame = DiskSpaceInformer.setupAndShowUI();
        }
        return this.testFrame;
    }

    @Test
    public void TestDiskInformerUserInterface(){
        //DiskSpaceInformer diskSpaceInformer = new DiskSpaceInformer();
        assertTrue(true) ;


    }

    @After
    public void tearDown(  ) throws Exception {
        if (this.testFrame != null) {
            this.testFrame.dispose(  );
            this.testFrame = null;
        }
    }



}
