package Server;

import DataUtils.Cache;
import RESPUtils.RESPSerializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class RedisController {

    private static final String CRLF = "\r\n";
    private final Cache cache;

    private final RESPSerializer serializer = new RESPSerializer();

    public RedisController(Cache cache) {
        this.cache = cache;
    }

    public void fulfillClientRequest(String[] args, DataOutputStream out) {
        switch (args[0].toLowerCase()) {
            case "ping" -> this.sendResponse(this.pong(), out);
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

            default -> this.sendResponse(serializer.serializeError("Command " + args[0] + " not recognized."), out);
        }
    }

    public String echo(String message) {
        return serializer.serializeSimpleString(message);
    }

    private String pong() {
        return serializer.serializeSimpleString("PONG");
    }
    public String set(String[] args) {
        String key = args[1];
        this.cache.put(key, args[2]);
        if (args.length > 3) {

            if (args.length < 5)
                return serializer.serializeError("Expiry time not provided.");

            if (!(args[3].equalsIgnoreCase("px") || args[3].equalsIgnoreCase("ex")))
                return serializer.serializeError("Option " + args[3] + " not recognized.");

            long timeInMs = Long.parseLong(args[4]);
            this.setExpiry(key, timeInMs, args[3]);
        }
        return serializer.ok();
    }

    public String get(String key) {
        if (key == null || key.length() == 0)
            return serializer.serializeError("No value provided.");
        return serializer.serializeBulkString(this.cache.get(key));
    }

    public String mset(String[] args) {
        if ((args.length - 1) % 2 != 0) return serializer.serializeError("Not enough arguments.");

        for (int i = 1; i < args.length; i += 2) {
            System.out.println(args[i] + " : " + args[i+1]);
            this.cache.put(args[i], args[i + 1]);
        }

        return serializer.ok();
    }

    public String hset(String[] args) {
        if (args.length % 2 != 0) return serializer.serializeError("Not enough arguments.");

        Map<String, String> hash = new HashMap();
        String hashName = args[1];

        for (int i = 2; i < args.length - 1; i += 2) {
            hash.put(args[i], args[i + 1]);
        }

        this.cache.putHash(hashName, hash);

        return serializer.ok();
    }

    public String hget(String[] args) {
        String hashName = args[1];
        if (hashName == null || hashName.length() == 0)
            return serializer.serializeError("No value provided.");

        if (!(this.cache.contains(hashName)))
            return serializer.serializeBulkString(null);

        String key = args[2];
        Map<String, String> hash = this.cache.getHash(hashName);
        if (!(hash.containsKey(key)))
            return serializer.serializeBulkString(null);

        return serializer.serializeBulkString(hash.get(key));
    }

    public String keys() {
        Set<String> keys = this.cache.keys();

        if (keys.size() == 0) return "*-1" + CRLF;

        StringBuilder keyList = new StringBuilder("*");
        int numberOfKeys = 0;
        for (String key : keys) {
            keyList.append(++numberOfKeys + ") ").append(key).append(CRLF);
        }

        return keyList.toString();
    }

    public String exists(String key) {
        if (this.cache.contains(key))
            return serializer.serializeInteger(1);
        return serializer.serializeInteger(0);
    }

    public String delete(String[] args) {
        int numberOfKeysDeleted = 0;
        for (int i = 1; i < args.length; i++) {
            if (this.cache.contains(args[i])) {
                this.cache.delete(args[i]);
                numberOfKeysDeleted++;
            }
        }

        return serializer.serializeInteger(numberOfKeysDeleted);
    }

    public String flushAll() {
        for (String key : this.cache.keys()) {
            this.cache.delete(key);
        }
        return serializer.ok();
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
