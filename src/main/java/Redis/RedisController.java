package Redis;

import DataUtils.Cache;
import DataUtils.TimeUtils;

import java.util.Timer;
import java.util.TimerTask;

public class RedisController {
    private Cache cache;

    public RedisController(Cache cache) {
        this.cache = cache;
    }
    public void set(String key, String value) {
        this.cache.put(key, value);
    }
    public String get(String key) {
        String value = this.cache.get(key);
        if (value != null) return "+" + value;
        return "+null";
    }

    public void delete(String key) {
        this.cache.delete(key);
    }

    public void deleteKeyAfterTimeMilliseconds(String key, long milliseconds) {
        TimerTask deleteKey = new TimerTask() {
            public void run() {
                cache.delete(key);
            }
        };
        new Timer("Timer").schedule(deleteKey, milliseconds);
    }

}
