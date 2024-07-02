package fr.dcotton.ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusMainTest
class DocCommandTest {
    @Test
    @Launch(value = {"doc"})
    void runTest(final LaunchResult pResult) {
        final var expected = String.join("\n",
                "Script Functions:",
                "  faker.regex(\"E-[A-Z]{2,4}#{2}\")                   \"E-AJD##\"                              Returns a new random string matching provided regex.",
                "  faker.uuid ()                                     \"fd48147a-58ba-461b-b71c-f44c89ba67ca\" Returns a new random UUID.",
                "  hash.md5   (\"SampleString\")                       \"ec1dd92925cb06934c047fb3f5380cba\"     Returns the MD5 hash of the string parameter.",
                "  hash.sha256(\"SampleString\")                       \"77b12c9c6213a05f...8b2c34769ec4fc20\"  Returns the SHA-256 hash of the string parameter.",
                "  hash.sha512(\"SampleString\")                       \"aee8e20df4b3ce73...e7e03c6fcda75961\"  Returns the SHA-512 hash of the string parameter.",
                "  pause      (3000)                                                                        Pause treatment during provided milliseconds.",
                "  time.format(\"yyyy-MM-dd HH:mm:ss\", 1708854821321) \"2024-02-25 10:53:41\"                  Returns the formatted date/string of a timestamp at current TimeZone.",
                "  time.now   ()                                     1708808432990                          Returns the current time in millis.");
        assertEquals(expected, pResult.getOutput());
    }
}
