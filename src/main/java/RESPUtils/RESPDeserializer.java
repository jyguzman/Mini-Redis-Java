package RESPUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RESPDeserializer {
    private static final String CRLF = "\r\n";

    public String formatRedisResponse(String redisResponse) {
        if (redisResponse == null || redisResponse.length() == 0) return null;

        String message = redisResponse.substring(1, redisResponse.length() - 2); // Remove first byte and CRLF

        StringBuilder result = new StringBuilder();
        char firstByte = redisResponse.charAt(0);
        switch (firstByte) {
            case '$' -> {
                if (message.equals("-1")) return "(nil)";
                int pointer = 0;
                while (message.charAt(pointer++) != '\n') {}
                return message.substring(pointer);
            }
            case ':' -> { return result.append("(integer) ").append(message).toString(); }
            case '-' -> { return result.append("ERROR ").append(message).toString(); }
            case '*' -> {
                String[] messageSplitByCRLF = message.split("\r\n");
                for (String string : messageSplitByCRLF) {
                    result.append(string.equals("-1") ? "(nil)" : string).append("\r\n");
                }
                return result.substring(0, result.length() - 2);
            }
            default -> { return message; } // if first byte is '+'
        }
    }
    // Deserialize RESP array from client
    public String[] deserializeRespArray(String respArray) {
        if (respArray == null || respArray.length() == 0) return new String[0];

        String numberOfStrings = "";
        int i = 1;
        while (Character.isDigit(respArray.charAt(i))) {
            numberOfStrings += respArray.charAt(i++);
        }
        String[] result = new String[Integer.parseInt(numberOfStrings)];

        int resultIndex = 0; i += CRLF.length();
        while (i < respArray.length()) {
            if (respArray.charAt(i) == '$') {
                String numChars = "";
                while (Character.isDigit(respArray.charAt(++i))) {
                    numChars += respArray.charAt(i) + "";
                }
                i += CRLF.length(); String word = "";
                for (int j = 0; j < Integer.parseInt(numChars); j++) {
                    word += respArray.charAt(i++);
                }
                result[resultIndex++] = word;
            }
            i++;
        }

        return result;
    }

    public static void main(String[] args) {
        RESPSerializer ser = new RESPSerializer();
        Scanner in = new Scanner(System.in);
        System.out.print("Input: ");
        String userIn = in.nextLine();
        String serD = ser.serializeToRespArray(userIn);
        System.out.println(serD);
        RESPDeserializer des = new RESPDeserializer();
        String[] arr = des.deserializeRespArray(serD);
        for (String str : arr) {
            System.out.print(str + " ");
        }
    }
 }
