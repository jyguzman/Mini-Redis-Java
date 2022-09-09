package DataUtils;

import java.util.Timer;
import java.util.TimerTask;

public class TimeUtils {
    private Cache cache;

    public TimeUtils(Cache cache) {
        this.cache = cache;
    }

    public void deleteKeyAfterTimeMilliseconds(String key, long milliseconds) {
        TimerTask deleteKey = new TimerTask() {
            public void run() {
                cache.delete(key);
            }
        };
        new Timer("Timer").schedule(deleteKey, milliseconds);
    }

    public void deleteKeyAfterTimeSeconds(String key, long seconds) {
        this.deleteKeyAfterTimeMilliseconds(key, seconds *  1000);
    }
}
