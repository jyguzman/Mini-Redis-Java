package Redis;

import DataUtils.Cache;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

public class RedisController {
    private Cache cache;

    public RedisController(Cache cache) {
        this.cache = cache;
    }

    public void fulfillClientRequest(String[] args, PrintWriter out) {
        switch (args[0].toLowerCase()) {
            case "echo":
                out.println(this.echo(args[1]));
                break;
            case "set":
                out.println(this.set(args));
                break;
            case "get":
                out.println(this.get(args[1]));
                break;
            default:
                out.println("+PONG");
                break;
        }
    }

    public String echo(String message) {
        return "+" + message;
    }
    public String set(String[] args) {
        this.cache.put(args[1], args[2]);
        if (args.length > 3 && args[3].equalsIgnoreCase("PX")) {
            //if (args.length == 4)
                //return "-ERROR: expiry time now provided.";
            this.deleteKeyAfterTimeMilliseconds(args[1], Long.parseLong(args[4]));
        }
        return "+OK";
    }

    public String get(String key) {
        String value = this.cache.get(key);
        if (value != null) return "+" + value;
        return "$-1";
    }

    public String delete(String key) {
        if (!(this.cache.contains(key)))
            return "-ERROR: key " + key + " not found.";
        this.cache.delete(key);
        return "+OK";
    }

    public String hset(String[] args) {

        return "+OK";
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
