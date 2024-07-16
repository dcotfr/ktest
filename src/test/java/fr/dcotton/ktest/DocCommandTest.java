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
                "  abs     (-3.14)                                3.14                                   Returns the absolute value of a number.",
                "  ceil    (3.14)                                 4                                      Returns the least integer value >= to given number.",
                "  concat  (\"Aaa\", \"Bbb\")                         \"AaaBbb\"                               Returns the concatenation of 2 strings.",
                "  decode64(\"VGV4dA==\")                           \"Text\"                                 Returns the decoded text of a base64 encoding string.",
                "  encode64(\"SampleString\")                       \"U2FtcGxlU3RyaW5n\"                     Returns the base64 encoding of a string.",
                "  env     (\"PATH\")                               \"C:\\Windows\\...\"                       Returns the value of an ENV variable.",
                "  floor   (3.14)                                 3                                      Returns the greatest integer value <= to given number.",
                "  left    (\"Sample\", 3)                          \"Sam\"                                  Returns the x first characters of a string.",
                "  lower   (\"ToLower\")                            \"tolower\"                              Returns the lower cased string.",
                "  ltrim   (\" Test \")                             \"Test \"                                Returns the with all left spaces removed.",
                "  max     (5, -2)                                5                                      Returns the maximal value of 2 numbers.",
                "  md5     (\"SampleString\")                       \"ec1dd92925cb06934c047fb3f5380cba\"     Returns the MD5 hash of the string parameter.",
                "  min     (5, -2)                                -2                                     Returns the minimal value of 2 numbers.",
                "  now     ()                                     1708808432990                          Returns the current time in millis.",
                "  pause   (3000)                                                                        Pause treatment during provided milliseconds.",
                "  regexgen(\"E-[A-Z]{2,4}#{2}\")                   \"E-AJD##\"                              Returns a new random string matching provided regex.",
                "  right   (\"Sample\", 3)                          \"ple\"                                  Returns the x last characters of a string.",
                "  round   (2.43)                                 2                                      Returns the nearest integer, rounding half away from zero.",
                "  rtrim   (\" Test \")                             \" Test\"                                Returns the with all right spaces removed.",
                "  sha256  (\"SampleString\")                       \"77b12c9c6213a05f...8b2c34769ec4fc20\"  Returns the SHA-256 hash of the string parameter.",
                "  sha512  (\"SampleString\")                       \"aee8e20df4b3ce73...e7e03c6fcda75961\"  Returns the SHA-512 hash of the string parameter.",
                "  time2txt(\"yyyy-MM-dd HH:mm:ss\", 1708854821321) \"2024-02-25 10:53:41\"                  Returns the formatted date/string of a timestamp at current TimeZone.",
                "  upper   (\"ToUpper\")                            \"TOUPPER\"                              Returns the upper cased string.",
                "  uuid    ()                                     \"fd48147a-58ba-461b-b71c-f44c89ba67ca\" Returns a new random UUID.");
        assertEquals(expected, pResult.getOutput());
    }
}
