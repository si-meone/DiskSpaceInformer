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
        TextFormatter formatter = new TextFormatter();
         assertThat(formatter.readableFileSize(1023), is("1,023 B"));
    }

    @Test
    public void testCheckRootDrivesSpace() throws Exception {
        TextFormatter formatter = new TextFormatter();
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
