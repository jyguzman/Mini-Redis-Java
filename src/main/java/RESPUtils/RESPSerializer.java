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
        BULK_STRING, SIMPLE_STRING, ERROR, INTEGER, RESP_ARRAY
    }

    public RESPSerializer() {
        this.p = Pattern.compile(regex);
    }

    private char getFirstByte(ResponseType type) {
        switch (type) {
            case BULK_STRING -> { return '$'; }
            case SIMPLE_STRING -> { return '+'; }
            case INTEGER -> { return ':'; }
            case RESP_ARRAY -> { return '*'; }
            default -> { return '-'; }
        }
    }

    private String serializeMessage(String message, ResponseType type) {
        if (message == null) {
            return "$-1\r\n";
        }
        StringBuilder result = new StringBuilder(Character.toString(this.getFirstByte(type)));
        if (type == ResponseType.BULK_STRING) {
            result.append(message.length()).append(CRLF);
        }
        return result.append(message).append(CRLF).toString();
    }

    public String serializeSimpleString(String string) {
        return this.serializeMessage(string, ResponseType.SIMPLE_STRING);
    }

    public String serializeBulkString(String string) {
        return this.serializeMessage(string, ResponseType.BULK_STRING);
    }

    public String serializeInteger(int integer) {
        return this.serializeMessage(Integer.toString(integer), ResponseType.INTEGER);
    }

    public String serializeArray(String respArray) {
        return this.serializeMessage(respArray, ResponseType.RESP_ARRAY);
    }

    public String serializeError(String error) {
        return this.serializeMessage(error, ResponseType.ERROR);
    }

    public String ok() {
        return this.serializeSimpleString("OK");
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
        RESPSerializer ser = new RESPSerializer();
        System.out.print(ser.serializeInteger(1));
    }
}

