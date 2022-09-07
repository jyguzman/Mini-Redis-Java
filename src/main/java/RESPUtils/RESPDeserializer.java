package RESPUtils;
public class RESPDeserializer {
    private static final String CLRF = "\r\n";
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
                i += CLRF.length();
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
        String x = "ECHO hey \\r";
        RESPDeserializer ser = new RESPDeserializer();
        String[] arr = ser.deserializeRespArray("*1\r\n$15\r\n\"{\"0\" : \"\\r\" }\"");
        for (String str : arr) {
            System.out.print(str + " ");
        }
    }
 }
