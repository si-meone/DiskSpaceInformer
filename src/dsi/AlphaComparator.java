package dsi;

import java.util.Comparator;
import java.util.Map;

class AlphaComparator implements Comparator<String> {

    Map<String, Long> base;

    public AlphaComparator(Map<String, Long> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        return (a.toLowerCase()).compareTo(b.toLowerCase());
    }

}