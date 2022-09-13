package Redis;

import DataUtils.Cache;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RedisController {
    private final Cache cache;

    public RedisController(Cache cache) {
        this.cache = cache;
    }

    public void fulfillClientRequest(String[] args, DataOutputStream out) {
        switch (args[0].toLowerCase()) {
            case "ping" -> this.sendResponse("+PONG", out);
            case "echo" -> this.sendResponse(this.echo(args[1]), out);

            case "keys" -> this.sendResponse(this.keys(), out);
            case "exists" -> this.sendResponse(this.exists(args[1]), out);

            case "set" -> this.sendResponse(this.set(args), out);
            case "mset" -> this.sendResponse(this.mset(args), out);
            case "get" -> this.sendResponse(this.get(args[1]), out);

            case "hset" -> this.sendResponse(this.hset(args), out);
            case "hget" -> this.sendResponse(this.hget(args), out);

            case "del" -> this.sendResponse(this.delete(args), out);
            case "flushall" -> this.sendResponse(this.flushAll(), out);

            default -> this.sendResponse("-ERROR: command " + args[0] + " not recognized.", out);
        }
    }

    public String echo(String message) {
        return "$" + message;
    }
    public String set(String[] args) {
        String key = args[1];
        this.cache.put(key, args[2]);
        if (args.length > 3) {

            if (args.length < 5)
                return "-ERROR: expiry time not provided.";
            if (!(args[3].equalsIgnoreCase("px") || args[3].equalsIgnoreCase("ex")))
                return "-ERROR: option " + args[3] + " not recognized.";

            long timeInMs = Long.parseLong(args[4]);
            this.setExpiry(key, timeInMs, args[3]);
        }
        return "+OK";
    }

    public String get(String key) {
        String value = this.cache.get(key);
        if (value != null) return "$" + value;
        return "$-1";
    }

    public String mset(String[] args) {
        if ((args.length - 1) % 2 != 0)
            return "-ERROR Not enough arguments.";

        for (int i = 1; i < args.length - 1; i += 2) {
            this.cache.put(args[i], args[i + 1]);
        }

        return "+OK";
    }

    public String hset(String[] args) {
        if (args.length % 2 != 0)
            return "-ERROR Not enough arguments.";

        Map<String, String> hash = new HashMap();
        String hashName = args[1];

        for (int i = 2; i < args.length - 1; i += 2) {
            hash.put(args[i], args[i + 1]);
        }

        this.cache.putHash(hashName, hash);

        return "+OK";
    }

    public String hget(String[] args) {
        String hashName = args[1];
        if (!(this.cache.containsHash(hashName)))
            return "$-1";

        String key = args[2];
        Map<String, String> hash = this.cache.getHash(hashName);
        if (!(hash.containsKey(key)))
            return "$-1";

        return "$" + hash.get(key);
    }

    public String keys() {
        Set<String> keys = this.cache.keys();
        Set<String> hashNames = this.cache.hashes();

        if (keys.size() == 0 && hashNames.size() == 0)
            return "*-1\r\n";

        StringBuilder keyList = new StringBuilder("*");
        int i = 0;
        for (String key : keys) {
            keyList.append(++i + ") " + key + "\r\n");
        }
        for (String key : hashNames) {
            keyList.append(++i + ") " + key + "\r\n");
        }
        return keyList.toString();
    }

    public String exists(String key) {
        if (this.cache.contains(key))
            return ":1\r\n";
        return ":0\r\n";
    }

    public String delete(String[] args) {
        int numKeysDeleted = 0;
        for (int i = 1; i < args.length; i++) {
            if (this.cache.contains(args[i])) {
                this.cache.delete(args[i]);
                numKeysDeleted++;
            }

            if (this.cache.containsHash(args[i])) {
                this.cache.deleteHash(args[i]);
                numKeysDeleted++;
            }
        }

        return ":" + numKeysDeleted;
    }

    public String flushAll() {
        for (String key : this.cache.keys()) {
            this.cache.delete(key);
        }
        return "+OK";
    }

    private void deleteKeyAfterTimeMilliseconds(String key, long milliseconds) {
        TimerTask deleteKey = new TimerTask() {
            public void run() {
                cache.delete(key);
            }
        };
        new Timer().schedule(deleteKey, milliseconds);
    }

    private void setExpiry(String key, long ms, String type) {
        switch (type) {
            case "px" -> this.deleteKeyAfterTimeMilliseconds(key, ms);
            case "ex" -> this.deleteKeyAfterTimeMilliseconds(key, ms * 1000);
        }
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
