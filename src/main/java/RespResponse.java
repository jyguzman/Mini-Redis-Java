public class RespResponse {
    private final String string;
    private final RespResponseType type;

    public enum RespResponseType {
        SIMPLE_STRING, ERROR, INTEGER, BULK_STRING, ARRAY
    }

    public RespResponse(String string, RespResponseType type) {
        this.string = string;
        this.type = type;
    }

    private Character getFirstByte() {
        switch (this.type) {
            case SIMPLE_STRING:
                return '+';
            case INTEGER:
                return ':';
            case  BULK_STRING:
                return '$';
            case ARRAY:
                return '*';
        }
        return '-';
    }

    public String sendRespResponse() {
        Character firstByte = this.getFirstByte();
        return firstByte.toString() + this.string + "/r/n";
    }
}
