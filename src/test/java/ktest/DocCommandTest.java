package ktest;

import ktest.core.AnsiColor;
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
                        DocCommand.SAMPLE_CONFIG, DocCommand.SAMPLE_TEST_CASE, DocCommand.OPERATORS_DOC, DocCommand.CONDITIONS_DOC,
                        "Scripting Functions:",
                        " FAKER:",
                        "  regexgen(\"E-[A-Z]{2,4}#{2}\")                   \"E-AJD##\"                              Returns a new random string matching provided regex.",
                        "  uuid    ()                                     \"fd48147a-58ba-461b-b71c-f44c89ba67ca\" Returns a new random UUID.",
                        " HASH:",
                        "  aesdec  (\"B64CryptedIn\", \"B64Key\")             \"ClearText\"                            Returns the decrypted value of base64-AES256 encrypted value.",
                        "  aesenc  (\"ClearText\", \"B64Key\")                \"B64CryptedOut\"                        Returns the base64 form of the value encrypted with AES256.",
                        "  aeskey  ()                                     \"ygrS4...ijP8=\"                        Returns a new random base-64-encoded AES256 key.",
                        "  crc32   (\"SampleString\")                       \"3ca8bf4\"                              Returns the CRC-32 hash of the string parameter.",
                        "  decode64(\"VGV4dA==\")                           \"Text\"                                 Returns the decoded text of a base64 encoding string.",
                        "  encode64(\"SampleString\")                       \"U2FtcGxlU3RyaW5n\"                     Returns the base64 encoding of a string.",
                        "  md5     (\"SampleString\")                       \"ec1dd92925cb06934c047fb3f5380cba\"     Returns the MD5 hash of the string parameter.",
                        "  sha1    (\"SampleString\")                       \"ac7fc7261c573830...f20bf0d74d1443cd\"  Returns the SHA-1 hash of the string parameter.",
                        "  sha256  (\"SampleString\")                       \"77b12c9c6213a05f...8b2c34769ec4fc20\"  Returns the SHA-256 hash of the string parameter.",
                        "  sha512  (\"SampleString\")                       \"aee8e20df4b3ce73...e7e03c6fcda75961\"  Returns the SHA-512 hash of the string parameter.",
                        " LOG:",
                        "  debug   (2+3)                                  5                                      Logs the concatenation of evaluated expression(s) as DEBUG output.",
                        "  error   (\"Failed\")                             Failed                                 Logs the concatenation of evaluated expression(s) as ERROR output.",
                        "  info    (\"r=\", 2*3)                            r=6                                    Logs the concatenation of evaluated expression(s) as INFO output.",
                        "  trace   (\"A\", \"b\")                             Ab                                     Logs the concatenation of evaluated expression(s) as TRACE output.",
                        "  warn    ()                                                                            Logs the concatenation of evaluated expression(s) as WARN output.",
                        " MATH:",
                        "  abs     (-3.14)                                3.14                                   Returns the absolute value of a number.",
                        "  ceil    (3.14)                                 4                                      Returns the least integer value >= to given number.",
                        "  floor   (3.14)                                 3                                      Returns the greatest integer value <= to given number.",
                        "  max     (5, -2)                                5                                      Returns the maximal value of 1 or more numbers.",
                        "  min     (5, -2, 0)                             -2                                     Returns the minimal value of 1 or more numbers.",
                        "  round   (2.43)                                 2                                      Returns the nearest integer, rounding half away from zero.",
                        " MISC:",
                        "  env     (\"SHELL\")                              \"/bin/bash\"                            Returns the value of an ENV variable.",
                        "  goto    (\"NameOfStep\")                                                                Jump and continue to named Step.",
                        "  pause   (3000)                                                                        Pause treatment during provided milliseconds.",
                        " TEXT:",
                        "  concat  (\"Aaa\", \"Bbb\",...)                     \"AaaBbb\"                               Returns the concatenation of multiple strings.",
                        "  left    (\"Sample\", 3)                          \"Sam\"                                  Returns the x first characters of a string.",
                        "  length  (\"Short text\")                         10                                     Returns the length of a string.",
                        "  lower   (\"ToLower\")                            \"tolower\"                              Returns the lower cased string.",
                        "  ltrim   (\" Test \")                             \"Test \"                                Returns the string with all left spaces removed.",
                        "  replace (\"ABAB\", \"B\", \"a\")                     \"AaAa\"                                 Returns a new string with old substring replaced by new substring.",
                        "  right   (\"Sample\", 3)                          \"ple\"                                  Returns the x last characters of a string.",
                        "  rtrim   (\" Test \")                             \" Test\"                                Returns the string with all right spaces removed.",
                        "  upper   (\"ToUpper\")                            \"TOUPPER\"                              Returns the upper cased string.",
                        " TIME:",
                        "  now     ()                                     1708808432990                          Returns the current time in millis.",
                        "  time2txt(\"yyyy-MM-dd HH:mm:ss\", 1708854821321) \"2024-02-25 10:53:41\"                  Returns the formatted date/string of a timestamp at current TimeZone.",
                        "  txt2time(\"yyyy/MM/dd\", \"2024/07/17\")           1721174400000                          Returns the timestamp of a formatted date string.")
                .replace(AnsiColor.BRIGHTYELLOW, "").replace(AnsiColor.WHITE, "");
        assertEquals(expected, pResult.getOutput());
    }
}
