package ktest.json;

import ktest.core.KTestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonAssertTest {
    private static final String ACTUAL = """
            {
              "lastname": "Dupont",
              "firstname": "Pierre",
              "address": {
                "lines": [ "Lieu dit", "10 avenue du lac" ],
                "zip": 13520,
                "city": "Paris",
                "country": null
              }
            }""";

    @Test
    void matchTest() {
        assertTrue(JsonAssert.contains("{\"lastname\":\"Dupont\"}", ACTUAL).isEmpty());
        assertTrue(JsonAssert.contains("{\"lastname\":\"Dupont\",\"address\":{\"zip\":13520}}", ACTUAL).isEmpty());
        assertTrue(JsonAssert.contains("{\"address\":{\"lines\":[\"Lieu dit\",\"10 avenue du lac\"]}}", ACTUAL).isEmpty());
        assertTrue(JsonAssert.contains("{\"address\":{\"country\":null}}", ACTUAL).isEmpty());
        assertTrue(JsonAssert.contains("{\"sender\":\"Source\",\"eventTsp\":1714424613038,\"body\":{\"code\":\"P1\",\"label\":\"Product 1\"}}", "{\"sender\": \"Source\", \"eventType\": \"CREATE\", \"eventTsp\": 1714424613038, \"body\": {\"code\": \"P1\", \"label\": \"Product 1\", \"commandAt\": 1714424612038, \"sentAt\": 1714424612138, \"weight\": 12030.5}}").isEmpty());
    }

    @Test
    void mismatchTest() {
        var res = JsonAssert.contains("{\"nom\":\"Dupont\"}", ACTUAL);
        assertEquals(1, res.size());
        assertEquals(new MissingField("nom"), res.getFirst());
        assertEquals("Missing field 'nom'.", res.getFirst().message());

        res = JsonAssert.contains("{\"lastname\":\"Martin\"}", ACTUAL);
        assertEquals(1, res.size());
        assertEquals(new ContentMismatch("lastname", "Martin", "Dupont"), res.getFirst());
        assertEquals("Content mismatch of field 'lastname': 'Martin' expected, 'Dupont' found.", res.getFirst().message());

        res = JsonAssert.contains("{\"address\":{\"lines\":[]}}", ACTUAL);
        assertEquals(1, res.size());
        assertEquals(new BadArraySize("address.lines", "expected 0 value but got 2"), res.getFirst());
        assertEquals("Bad array size of field 'address.lines': expected 0 value but got 2.", res.getFirst().message());

        res = JsonAssert.contains("{\"address\":{\"lines\":[\"Rue du pont\"]}}", ACTUAL);
        assertEquals(1, res.size());
        assertEquals(new BadArraySize("address.lines", "expected 1 value but got 2"), res.getFirst());
        assertEquals("Bad array size of field 'address.lines': expected 1 value but got 2.", res.getFirst().message());

        res = JsonAssert.contains("{\"address\":{\"lines\":[\"10 avenue du lac\",\"Rue du pont\"]}}", ACTUAL);
        assertEquals(1, res.size());
        assertEquals(new MissingField("Rue du pont"), res.getFirst());
        assertEquals("Missing field 'Rue du pont'.", res.getFirst().message());
    }

    @Test
    void invalidJsonTest() {
        try {
            JsonAssert.contains("", ACTUAL);
            fail();
        } catch (final KTestException e) {
            assertEquals("Invalid JSON.", e.getMessage());
        }
    }
}
