package dsi;

import java.util.*;

import dsi.SizeComparator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SizeComparatorTest {

    private HashMap<String,Long> foldersSizes;

    @Before()
    public void setUp() throws Exception {
        foldersSizes = new HashMap<String, Long>();
        foldersSizes.put("b", 2L);
        foldersSizes.put("a", 1L);
        foldersSizes.put("c", 3L);

    }

    @Test()
    public void testWhenApplyingComparatorGetOrderBasedOnLargestValueFirst() throws Exception {
        SizeComparator vc = new SizeComparator(foldersSizes);
        Map<String, Long> sortedMap = new TreeMap<String, Long>(vc);
        sortedMap.putAll(foldersSizes);
        assertThat(sortedMap.keySet().toString(), is("[c, b, a]"));
    }
}
