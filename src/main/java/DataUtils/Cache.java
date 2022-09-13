package DataUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private Map<String, String> cache = new ConcurrentHashMap();
    private Map<String, Map<String, String>> cacheOfHashes = new HashMap();
    public void put(String key, String value) {
        this.cache.put(key, value);
    }

    public void putHash(String key, Map<String, String> hash) {
        this.cacheOfHashes.put(key, hash);
    }

    public String get(String key) {
        return this.cache.get(key);
    }
    public Map<String, String> getHash(String key) {
        return this.cacheOfHashes.get(key);
    }

    public void delete(String key) {
        this.cache.remove(key);
    }
    public void deleteHash(String key) {
        this.cacheOfHashes.remove(key);
    }

    public boolean contains(String key) {
        return this.cache.containsKey(key);
    }

    public boolean containsHash(String key) {
        return this.cacheOfHashes.containsKey(key);
    }

    public Set<String> keys() {
        return this.cache.keySet();
    }
    public Set<String> hashes() {
        return this.cacheOfHashes.keySet();
    }
}
