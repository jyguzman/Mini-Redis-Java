package RESPUtils;
public class RESPDeserializer {
    private static final String CRLF = "\r\n";
    public String[] deserializeRespArray(String respArray) {
        if (respArray == null || respArray.length() == 0) return new String[0];
        int numberOfStrings = Integer.parseInt(respArray.charAt(1) + "");
        String[] result = new String[numberOfStrings];
        int i = 3; int resultIndex = 0;

        while (i < respArray.length()) {
            if (respArray.charAt(i) == '$') {
                String numChars = "";
                while (Character.isDigit(respArray.charAt(++i))) {
                    numChars += respArray.charAt(i) + "";
                }
                i += CRLF.length();
                String word = "";
                for (int j = 0; j < Integer.parseInt(numChars); j++) {
                    if (respArray.charAt(i) == '\\') {
                        if (respArray.charAt(i) == 'n') {
                            word += '\n'; i += 2;
                        }
                    } else {
                        word += respArray.charAt(i++);
                    }
                }
                result[resultIndex] = word;
                resultIndex++;
            }
            i++;
        }
        return result;
    }

    public static void main(String[] args) {
        RESPSerializer ser = new RESPSerializer();
        String serD = ser.serializeToRespArray("SET name jordie");
        System.out.println(serD);
        RESPDeserializer des = new RESPDeserializer();
        String[] arr = des.deserializeRespArray(serD);
        for (String str : arr) {
            System.out.print(str + " ");
        }
    }
 }
