package dsi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = { DiskSpaceInformerTest.class,
        FileSystemTreeModelTest.class,
        FileSystemVisitorTest.class,
        SizeComparatorTest.class,
        PrettyPrintTest.class})
public class TestSuite {


    public TestSuite() {
    }
}