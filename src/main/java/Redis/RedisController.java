package Redis;

import DataUtils.Cache;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class RedisController {
    private Cache cache;

    public RedisController(Cache cache) {
        this.cache = cache;
    }

    public void fulfillClientRequest(String[] args, DataOutputStream out) {
        switch (args[0].toLowerCase()) {
            case "echo":
                this.sendResponse(this.echo(args[1]), out);
                break;
            case "set":
                this.sendResponse(this.set(args), out);
                break;
            case "get":
                this.sendResponse(this.get(args[1]), out);
                break;
            case "delete":
                this.sendResponse(this.delete(args[1]), out);
                break;
            case "flushall":
                this.sendResponse(this.flushAll(), out);
                break;
            default:
                this.sendResponse("+PONG", out);
                break;
        }
    }

    public String echo(String message) {
        return "+" + message;
    }
    public String set(String[] args) {
        this.cache.put(args[1], args[2]);
        if (args.length > 3 && args[3].equalsIgnoreCase("PX")) {
            if (args.length < 5)
                return "-ERROR: expiry time not provided.";
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

    public String flushAll() {
        for (String key : this.cache.keys()) {
            this.cache.delete(key);
        }
        return "+OK";
    }

    public String hset(String[] args) {
        Map<String, Map<String, String>> data = new ConcurrentHashMap();
        Map<String, String> innerData = new ConcurrentHashMap();

        for (int i = 2; i < args.length; i += 2) {
            innerData.put(args[i], args[i + 1]);
        }

        return "+OK";
    }

    public void deleteKeyAfterTimeMilliseconds(String key, long milliseconds) {
        TimerTask deleteKey = new TimerTask() {
            public void run() {
                cache.delete(key);
            }
        };
        new Timer().schedule(deleteKey, milliseconds);
    }

    public void sendResponse(String response, DataOutputStream out) {
        try {
            out.writeUTF(response);
            out.flush();
        } catch(IOException e) {
            System.out.println("Trouble sending response.");
        }
    }

}
