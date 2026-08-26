import java.util.*;

class AllOne {

    private Map<String, Integer> count;
    private TreeMap<Integer, HashSet<String>> keys;

    public AllOne() {
        count = new HashMap<>();
        keys = new TreeMap<>();
    }

    public void inc(String key) {

        count.put(key, count.getOrDefault(key, 0) + 1);
        int cnt = count.get(key);

        keys.putIfAbsent(cnt, new HashSet<>());
        keys.get(cnt).add(key);

        if (cnt != 1) {
            keys.get(cnt - 1).remove(key);

            if (keys.get(cnt - 1).isEmpty()) {
                keys.remove(cnt - 1);
            }
        }
    }

    public void dec(String key) {

        int prevCount = count.get(key);
        count.put(key, prevCount - 1);

        keys.get(prevCount).remove(key);

        if (keys.get(prevCount).isEmpty()) {
            keys.remove(prevCount);
        }

        if (count.get(key) == 0) {
            count.remove(key);
        } else {
            keys.putIfAbsent(prevCount - 1, new HashSet<>());
            keys.get(prevCount - 1).add(key);
        }
    }

    public String getMaxKey() {

        if (keys.isEmpty())
            return "";

        return keys.lastEntry().getValue().iterator().next();
    }

    public String getMinKey() {

        if (keys.isEmpty())
            return "";

        return keys.firstEntry().getValue().iterator().next();
    }
}