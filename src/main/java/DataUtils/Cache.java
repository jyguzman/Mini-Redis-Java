package DataUtils;

import java.util.Map;
import java.util.HashMap;

public class Cache {
    private Map<String, String> cache = new HashMap();

    public void put(String key, String value) {
        cache.put(key, value);
    }

    public String get(String key) {
        return cache.get(key);
    }
}
