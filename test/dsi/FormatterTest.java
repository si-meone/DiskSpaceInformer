package dsi;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

//TODO increase testing ...
public class FormatterTest
{
    @Test
    public void testUnderOneKBShouldReadInBytes() throws Exception {
        Formatter formatter = new TextFormatter();
         assertThat(formatter.readableFileSize(1023), is("1,023 B"));
    }

    @Test
    //TODO: need a better test for debug mode
    public void testDebugUnderOneKBShouldReadInBytes() throws Exception {
        Formatter formatter = new TextDebugFormatter();
        assertThat(formatter.readableFileSize(1023), is("1,023 B"));
    }

    @Test
    public void testDebugFormatIsTabDelimited() throws Exception {
        Formatter formatter = new TextDebugFormatter();
        Map<String, Long> sortedFileFolderSizes = new HashMap<String, Long>();
        sortedFileFolderSizes.put("a", 612L);    //To change body of overridden methods use File | Settings | File Templates.
        sortedFileFolderSizes.put("b", 412L);    //To change body of overridden methods use File | Settings | File Templates.
        sortedFileFolderSizes.put("c", 312L);    //To change body of overridden methods use File | Settings | File Templates.
        sortedFileFolderSizes.put("d", 212L);    //To change body of overridden methods use File | Settings | File Templates.
        String format = formatter.format(new File("/"), 2048L, sortedFileFolderSizes, "File Exception");
        assertThat("does not contain correct format or text", format, containsString("2 KB\t\n" +
                "File Exceptiond\t212 B\n" +
                "b\t412 B\n" +
                "c\t312 B\n" +
                "a\t612 B\n"));
    }

    @Test
    public void testCheckRootDrivesSpace() throws Exception {
        Formatter formatter = new TextDebugFormatter();
        String format = formatter.format("/", 40980L, 10240L, 30740L);
        assertThat(format, is("──────────────\n" +
                "Checked: [ / ]  Free: [ 75% ]\n" +
                "──────────────\n" +
                "Total Space is: [ 40 KB ]\n" +
                "Used space is: [ 10 KB ] \n" +
                "Free space is: [ 30 KB ]\n" +
                "\n"));
    }


}
