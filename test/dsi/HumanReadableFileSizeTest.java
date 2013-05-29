package dsi;

import junit.extensions.TestSetup;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

//TODO increase testing ...
public class HumanReadableFileSizeTest
{
    private HumanReadableFileSize[] humanReadableFoldersSizes;

    @Before()
    public void setUp() throws Exception {
        humanReadableFoldersSizes =new HumanReadableFileSize[] {new HumanReadableFileSize(1L),
                new HumanReadableFileSize(1048576L),
                new HumanReadableFileSize(1024L)};
    }

    @Test
    public void testUnderOneKBShouldReadInBytes() throws Exception {
         assertThat(HumanReadableFileSize.readableFileSize(1023), is("1,023 B"));
    }

    @Test()
    public void humanReadableSizeSortShouldReturnLargestSizeFirst() throws Exception {
        Arrays.sort(humanReadableFoldersSizes, Collections.reverseOrder());
        assertThat(Arrays.toString(humanReadableFoldersSizes), equalTo("[1 MB, 1 KB, 1 B]"));
    }

}
