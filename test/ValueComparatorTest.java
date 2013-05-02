import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import static junit.framework.Assert.*;

public class ValueComparatorTest {

    private HashMap<String,Long> filesizes;

    @org.junit.Before
    public void setUp() throws Exception {
        filesizes = new HashMap<String, Long>();
        filesizes.put("b", 2L);
        filesizes.put("a", 1L);
        filesizes.put("c", 3L);

    }

    @org.junit.Test
    public void testWhenApplyingComparatorGetOrderBasedOnLargestValueFirst() throws Exception {
        throw new RuntimeException("You left things here");
    }
}
