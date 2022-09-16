package test;

import RESPUtils.RESPSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RESPSerializerTest {

    RESPSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new RESPSerializer();
    }

    @AfterEach
    void tearDown() {
        serializer = null;
    }

    @Test
    void serializeSimpleString() {
        String simple = serializer.serializeSimpleString("This is a response.");
        assertEquals("+This is a response.\r\n", simple);
    }

    @Test
    void serializeBulkString() {
        String nullString = serializer.serializeBulkString(null);
        assertEquals("$-1\r\n", nullString);

        String emptyString = serializer.serializeBulkString("");
        assertEquals("$0\r\n", emptyString);

        String name = serializer.serializeBulkString("Jordie Guzman");
        assertEquals("$13\r\nJordie Guzman\r\n", name);
    }

    @Test
    void serializeInteger() {
        String integer = serializer.serializeInteger(9029382);
        assertEquals( ":9029382\r\n", integer);
    }

    @Test
    void serializeError() {
        String error = serializer.serializeError("ERROR Not found.");
        assertEquals("-ERROR Not found.\r\n", error);
    }

    @Test
    void serializeToRespArray() {
        String commandRespArray = serializer.serializeToRespArray("SET name \"Jordie Guzman\"");
        assertEquals("*3\r\n$3\r\nSET\r\n$4\r\nname\r\n$15\r\n\"Jordie Guzman\"\r\n", commandRespArray);
    }
}