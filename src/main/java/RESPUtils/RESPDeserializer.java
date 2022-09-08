package RESPUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class RESPDeserializer {
    private static final String CRLF = "\r\n";
    public String[] deserializeRespArray(String respArray) {
        int numberOfStrings = Integer.parseInt(respArray.charAt(1) + "");
        String[] result = new String[numberOfStrings];
        int i = 4; int resultIndex = 0;

        while (i < respArray.length()) {
            if (respArray.charAt(i) == '$') {
                String numChars = "";
                while (Character.isDigit(respArray.charAt(++i))) {
                    numChars += respArray.charAt(i) + "";
                }
                i += CRLF.length();
                String word = "";
                for (int j = 0; j < Integer.parseInt(numChars); j++) {
                    word += respArray.charAt(i++);
                }
                result[resultIndex++] = word;
            }
            i++;
        }
        return result;
    }

    public List<String> deserializeRespArray(BufferedReader in) {
        int c = 0;
        int numStrings = 0;
        List<String> result = new ArrayList();
        try {
            in.read();
            numStrings = Integer.parseInt("" + (char)in.read());

            while (result.size() < numStrings) {
                c = in.read();
                if ((char) c == '$') {
                    StringBuilder word = new StringBuilder();
                    int numChars = Integer.parseInt("" + (char)in.read());
                    in.read(); in.read();
                    for (int i = 0; i < numChars; i++) {
                        word.append((char) in.read());
                    }
                    result.add(word.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        RESPSerializer ser = new RESPSerializer();
        String serD = ser.serializeToRespArray("ECHO \"{\"water\" : \"hel l o\"}\"");
        System.out.println(serD);
        RESPDeserializer des = new RESPDeserializer();
        String[] arr = des.deserializeRespArray(serD);
        for (String str : arr) {
            System.out.print(str + " ");
        }
    }
 }
