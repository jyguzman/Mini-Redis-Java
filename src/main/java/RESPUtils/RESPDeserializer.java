package RESPUtils;
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

    public static void main(String[] args) {
        RESPSerializer ser = new RESPSerializer();
        String serD = ser.serializeToRespArray("ECHO \"{\"water\":\"hello \\r \"}\"");
        System.out.println(serD);
        RESPDeserializer des = new RESPDeserializer();
        String[] arr = des.deserializeRespArray(serD);
        for (String str : arr) {
            System.out.print(str + " ");
        }
    }
 }
