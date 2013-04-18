import java.util.Comparator;
import java.util.Map;

class ValueComparator implements Comparator<String> {

    Map<String, Long> base;

    public ValueComparator(Map<String, Long> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } //TODO: what about zero ?
    }

}