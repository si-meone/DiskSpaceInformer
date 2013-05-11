import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PrettyPrintTest
{
    @Test
    public void testUnderOneKBShouldReadInBytes() throws Exception {
         assertThat(PrettyPrint.readableFileSize(1023), is("1,023 B"));
    }
}
