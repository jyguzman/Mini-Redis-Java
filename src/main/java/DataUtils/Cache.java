package DataUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private Map<String, String> cache = new ConcurrentHashMap();

    public void put(String key, String value) {
        this.cache.put(key, value);
    }

    public String get(String key) {
        return this.cache.get(key);
    }

    public void delete(String key) {
        this.cache.remove(key);
    }

    public boolean contains(String key) {
        return this.cache.containsKey(key);
    }

    public Set<String> keys() {
        return this.cache.keySet();
    }
}
