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
                " FAKER:",
                "  regexgen(\"E-[A-Z]{2,4}#{2}\")                   \"E-AJD##\"                              Returns a new random string matching provided regex.",
                "  uuid    ()                                     \"fd48147a-58ba-461b-b71c-f44c89ba67ca\" Returns a new random UUID.",
                " HASH:",
                "  crc32   (\"SampleString\")                       \"3ca8bf4\"                              Returns the CRC-32 hash of the string parameter.",
                "  decode64(\"VGV4dA==\")                           \"Text\"                                 Returns the decoded text of a base64 encoding string.",
                "  encode64(\"SampleString\")                       \"U2FtcGxlU3RyaW5n\"                     Returns the base64 encoding of a string.",
                "  md5     (\"SampleString\")                       \"ec1dd92925cb06934c047fb3f5380cba\"     Returns the MD5 hash of the string parameter.",
                "  sha1    (\"SampleString\")                       \"ac7fc7261c573830...f20bf0d74d1443cd\"  Returns the SHA-1 hash of the string parameter.",
                "  sha256  (\"SampleString\")                       \"77b12c9c6213a05f...8b2c34769ec4fc20\"  Returns the SHA-256 hash of the string parameter.",
                "  sha512  (\"SampleString\")                       \"aee8e20df4b3ce73...e7e03c6fcda75961\"  Returns the SHA-512 hash of the string parameter.",
                " LOG:",
                "  debug   (2+3)                                  5                                      Display the evaluated expression as DEBUG log output.",
                "  error   (2+3)                                  5                                      Display the evaluated expression as ERROR log output.",
                "  info    (2+3)                                  5                                      Display the evaluated expression as INFO log output.",
                "  trace   (2+3)                                  5                                      Display the evaluated expression as TRACE log output.",
                "  warn    (2+3)                                  5                                      Display the evaluated expression as WARN log output.",
                " MATH:",
                "  abs     (-3.14)                                3.14                                   Returns the absolute value of a number.",
                "  ceil    (3.14)                                 4                                      Returns the least integer value >= to given number.",
                "  floor   (3.14)                                 3                                      Returns the greatest integer value <= to given number.",
                "  max     (5, -2)                                5                                      Returns the maximal value of 2 numbers.",
                "  min     (5, -2)                                -2                                     Returns the minimal value of 2 numbers.",
                "  round   (2.43)                                 2                                      Returns the nearest integer, rounding half away from zero.",
                " MISC:",
                "  env     (\"PATH\")                               \"C:\\Windows\\...\"                       Returns the value of an ENV variable.",
                "  pause   (3000)                                                                        Pause treatment during provided milliseconds.",
                " TEXT:",
                "  concat  (\"Aaa\", \"Bbb\")                         \"AaaBbb\"                               Returns the concatenation of 2 strings.",
                "  left    (\"Sample\", 3)                          \"Sam\"                                  Returns the x first characters of a string.",
                "  lower   (\"ToLower\")                            \"tolower\"                              Returns the lower cased string.",
                "  ltrim   (\" Test \")                             \"Test \"                                Returns the with all left spaces removed.",
                "  right   (\"Sample\", 3)                          \"ple\"                                  Returns the x last characters of a string.",
                "  rtrim   (\" Test \")                             \" Test\"                                Returns the with all right spaces removed.",
                "  upper   (\"ToUpper\")                            \"TOUPPER\"                              Returns the upper cased string.",
                " TIME:",
                "  now     ()                                     1708808432990                          Returns the current time in millis.",
                "  time2txt(\"yyyy-MM-dd HH:mm:ss\", 1708854821321) \"2024-02-25 10:53:41\"                  Returns the formatted date/string of a timestamp at current TimeZone.",
                "  txt2time(\"yyyy/MM/dd\", \"2024/07/17\")           1721174400000                          Returns the timestamp of a formatted date string.");
        assertEquals(expected, pResult.getOutput());
    }
}
