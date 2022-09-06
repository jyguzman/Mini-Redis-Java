package RESPUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.*;
public class RESPSerializer {
    private static final String CLRF = "\r\n";

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
        return firstByte + message.length() + CLRF + message + CLRF;
    }

    public String serializeToRespArray(String command) {
        List<String> clientMessageArgs = new ArrayList(Arrays.asList(command.split("\\s+")));
        StringBuilder respArray = new StringBuilder();
        respArray.append("*" + clientMessageArgs.size() + CLRF);
        String joinedBulkStrings = clientMessageArgs.stream()
                .map(string -> this.serializeString(string, "BulkString"))
                .collect(Collectors.joining(""));
        return respArray.append(joinedBulkStrings).toString();
    }

    public static void main(String[] args) {
        String x = "ECHO hey";
        RESPSerializer ser = new RESPSerializer();
        System.out.println(ser.serializeString("hello", "BulkString"));
        System.out.println(ser.serializeString("{ \"hello\": 5 }", "BulkString"));
        String res = ser.serializeToRespArray(x);
        System.out.println(("*2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n").equals(res));
    }
}
