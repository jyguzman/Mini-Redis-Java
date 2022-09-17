package test;

import RESPUtils.RESPDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RESPDeserializerTest {

    RESPDeserializer deserializer = null;
    @BeforeEach
    void setUp() {
        deserializer = new RESPDeserializer();
    }

    @AfterEach
    void tearDown() {
        deserializer = null;
    }

    @Test
    void formatRedisResponse() {
        String simpleStringResponse = "+SimpleString\r\n";
        assertEquals("SimpleString", deserializer.formatRedisResponse(simpleStringResponse));

        String integerResponse = ":69420\r\n";
        assertEquals("(integer) 69420", deserializer.formatRedisResponse(integerResponse));

        String errorResponse = "-This is an error.\r\n";
        assertEquals("ERROR This is an error.", deserializer.formatRedisResponse(errorResponse));

        String nullBulkString = "$-1\r\n";
        assertEquals("(nil)", deserializer.formatRedisResponse(nullBulkString));

        String bulkString = "$13\r\nJordie Guzman\r\n";
        assertEquals("Jordie Guzman", deserializer.formatRedisResponse(bulkString));
    }

    @Test
    void deserializeRespArray() {
        String respArray = "*3\r\n$3\r\nset\r\n$4\r\nname\r\n$6\r\njordie\r\n";
        assertArrayEquals(new String[] {"set", "name", "jordie"}, deserializer.deserializeRespArray(respArray));
    }
}