package dsi;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
}
