import java.util.Comparator;
import java.util.Map;

class ValueComparator implements Comparator<String> {

    Map<String, Long> base;

    public ValueComparator(Map<String, Long> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        return base.get(b).compareTo(base.get(a));
    }

}