package RESPUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;
public class RESPSerializer {
    private static final String CRLF = "\r\n";
    //private static final String regex = "(\".*?\"|[^\"\\s]+)+(?=\\s*|\\s*$)";
    private static final String regex = "(\".*\"|[^\"\\s]+)+(?=\\s*|\\s*$)"; // removed first question mark
    private Pattern p;

    private enum ResponseType {
        BULK_STRING, SIMPLE_STRING, ERROR, INTEGER
    }

    public RESPSerializer() {
        this.p = Pattern.compile(regex);
    }

    public String serializeString(String message, String type) {
        char firstByte = 'a';
        switch (type) {
            case "BulkString":
                firstByte = '$';
                break;
            case "SimpleString":
                firstByte = '+';
                break;
            case "Error":
                firstByte = '-';
                break;
            case "Integer":
                firstByte = ':';
                break;
        }
        return "" + firstByte + message.length() + CRLF + message + CRLF;
    }

    private String serializeBulkString(String message) {
        return "$" + message.length() + CRLF + message + CRLF;
    }

    public String serializeToRespArray(String message) {
        if (message == null || message.length() == 0) return null;
        List<String> clientMessageArgs = new ArrayList();

        Matcher matches = this.p.matcher(message);
        while (matches.find()) {
            clientMessageArgs.add(matches.group());
        }

        StringBuilder respArray = new StringBuilder();
        respArray.append("*" + clientMessageArgs.size() + CRLF);
        String joinedBulkStrings = clientMessageArgs.stream()
                .map(string -> this.serializeBulkString(string))
                .collect(Collectors.joining(""));

        return respArray.append(joinedBulkStrings).toString();
    }

    public static void main(String[] args) {
        String x = "ECHO \"{\"water\" : \"hel l o\"}\"";
        RESPSerializer ser = new RESPSerializer();
        //System.out.println(ser.serializeString("hello", "BulkString"));
        //System.out.println(ser.serializeString("{ \"hello\": 5 }", "BulkString"));
        String res = ser.serializeToRespArray(x);
        System.out.println(res);
        //System.out.println(("*2\r\n$4\r\nECHO\r\n$3\r\    nhey\r\n").equals(res));
    }
}

