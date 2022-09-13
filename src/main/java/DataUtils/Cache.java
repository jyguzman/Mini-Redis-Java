package DataUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private Set<String> keysInOrder = new LinkedHashSet();
    private Map<String, String> cache = new ConcurrentHashMap();
    private Map<String, Map<String, String>> cacheOfHashes = new HashMap();
    public void put(String key, String value) {
        this.keysInOrder.add(key);
        this.cache.put(key, value);
    }

    public void putHash(String key, Map<String, String> hash) {
        this.keysInOrder.add(key);
        this.cacheOfHashes.put(key, hash);
    }

    public String get(String key) {
        return this.cache.get(key);
    }
    public Map<String, String> getHash(String key) {
        return this.cacheOfHashes.get(key);
    }

    public void delete(String key) {
        if (this.cache.containsKey(key)) this.cache.remove(key);
        if (this.cacheOfHashes.containsKey(key)) this.cacheOfHashes.remove(key);
        this.keysInOrder.remove(key);
    }

    public boolean contains(String key) {
        return this.keysInOrder.contains(key);
    }

    public Set<String> keys() {
        return this.keysInOrder;
    }
    public Set<String> hashes() {
        return this.cacheOfHashes.keySet();
    }
}
