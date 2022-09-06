package RESPUtils;

public class RESPDeserializer {
    public String decodeBulkString(String bulkString) {
        int numBytes = Integer.parseInt(bulkString.charAt(1) + "");
        StringBuilder result = new StringBuilder();
        int stringSize = 0;
        for (int i = 4; bulkString.charAt(i) != '\r'; i++) {
            result.append(bulkString.charAt(i));
            stringSize++;
            if (stringSize > numBytes) {
                return "ERROR";
            }
        }
        return result.toString();
    }

    public String[] deserializeRespArray(String respArray) {
        String clrfsRemoved = respArray.replace("\r\n", " ");
        String trimmed = respArray.trim();
        /*int arraySize = Integer.parseInt(""+respArray.charAt(1));
        String[] result = ne




        w String[arraySize];
        int respArrayPointer = 2;
        while (respArrayPointer < respArray.length()) {
            if (respArray.charAt(respArrayPointer) == '$') {
                respArrayPointer += 4;
                while (respArray.charAt(respArrayPointer) != '\\') {

            }
        }*/
        return new String[0];
    }
 }
