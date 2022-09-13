package RESPUtils;
public class RESPDeserializer {
    private static final String CRLF = "\r\n";

    public String deserializeRedisResponse(String redisResponse) {
        if (redisResponse == null || redisResponse.length() == 0) return null;

        String message = redisResponse.substring(1, redisResponse.length() - 2); // Remove first byte and CRLF
        StringBuilder result = new StringBuilder();
        char firstByte = redisResponse.charAt(0);
        switch (firstByte) {
            case '$' -> { return message.equals("-1") ? "(nil)" : message.substring(3); }
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
        // Works only for single-digit numberOfStrings - extend to multiple
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
                    word += respArray.charAt(i);
                    i++;
                }
                System.out.println(word);
                result[resultIndex] = word;
                resultIndex++;
            }
            i++;
        }

        return result;
    }

    /*public static void main(String[] args) {
        RESPSerializer ser = new RESPSerializer();
        String serD = ser.serializeToRespArray("SET name jordie");
        System.out.println(serD);
        RESPDeserializer des = new RESPDeserializer();
        String[] arr = des.deserializeRespArray(serD);
        for (String str : arr) {
            System.out.print(str + " ");
        }
    }*/
 }
