package RESPUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;
import com.google.gson.Gson;
public class RESPSerializer {
    private static final String CLRF = "\r\n";
    private static final String regex = "(\".*?\"|[^\"\\s]+)+(?=\\s*|\\s*$)";
    private Pattern p;

    private Gson gson = new Gson();
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
        return "" + firstByte + message.length() + CLRF + message + CLRF;
    }

    public String serializeToRespArray(String message) {
        List<String> clientMessageArgs = new ArrayList();
        Matcher matches = this.p.matcher(message);
        while (matches.find()) {
            clientMessageArgs.add(matches.group());
        }

        StringBuilder respArray = new StringBuilder();
        respArray.append("*" + clientMessageArgs.size() + CLRF);
        String joinedBulkStrings = clientMessageArgs.stream()
                .map(string -> this.serializeString(string, "BulkString"))
                .collect(Collectors.joining(""));

        return gson.toJson(respArray.append(joinedBulkStrings).toString());
    }

    public static void main(String[] args) {
        String x = "SET \"{0 : \"\\rhello\"}\"";
        RESPSerializer ser = new RESPSerializer();
        //System.out.println(ser.serializeString("hello", "BulkString"));
        //System.out.println(ser.serializeString("{ \"hello\": 5 }", "BulkString"));
        String res = ser.serializeToRespArray(x);
        System.out.println(ser.gson.fromJson(res, String.class));
        //System.out.println(("*2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n").equals(res));
    }
}

