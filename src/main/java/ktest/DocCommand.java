package ktest;

import jakarta.inject.Inject;
import ktest.script.Context;
import ktest.script.func.FuncType;
import picocli.CommandLine;

import java.util.Comparator;

import static java.lang.Math.max;
import static ktest.MainCommand.VERSION;
import static ktest.core.AnsiColor.BRIGHTYELLOW;
import static ktest.core.AnsiColor.WHITE;

@CommandLine.Command(name = "doc", description = "Display of the full documentation.",
        mixinStandardHelpOptions = true, version = VERSION)
public class DocCommand implements Runnable {
    static final String SAMPLE_CONFIG = BRIGHTYELLOW + "Sample ktconfig.yml:" + WHITE + "\n"
            + "  registries:\n"
            + "    - name: pi_registry\n"
            + "      url: http://192.168.0.105:8081\n"
            + "      user: UserName\n"
            + "      password: UserPassword\n"
            + "    - name: registry_2\n"
            + "      ...\n"
            + "  brokers:\n"
            + "    - name: pi_broker\n"
            + "      bootstrap.servers: 192.168.0.105:9092\n"
            + "      registry: pi_registry\n"
            + "      sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username=\"USER\" password=\"PASSWORD\";\n"
            + "      sasl.mechanism: PLAIN\n"
            + "      security.protocol: SASL_SSL\n"
            + "      group.id: pi.ktest-group\n"
            + "    - name: local_broker\n"
            + "      ...\n"
            + "  environments:\n"
            + "    - name: pi\n"
            + "      onStart: |\n"
            + "        BROKER_USED=\"pi_broker\"\n"
            + "        ...\n"
            + "      onEnd: info(\"Test finished\")\n"
            + "    - name: dev\n"
            + "      ...\n";
    static final String SAMPLE_TEST_CASE = BRIGHTYELLOW + "Sample Test Case file:" + WHITE + "\n"
            + "  name: \"Name of the Test Case\"\n"
            + "  beforeAll: |\n"
            + "    TIMESTAMP = now()\n"
            + "    ...\n"
            + "  steps:\n"
            + "    - name: \"Name of Step 1\"\n"
            + "      before: RANDOM_UUID = uuid()\n"
            + "      broker: ktconfig_broker_name\n"
            + "      topic: DestinationTopicName\n"
            + "      keySerde: STRING\n"
            + "      valueSerde: AVRO\n"
            + "      action: SEND\n"
            + "      record:\n"
            + "        headers:\n"
            + "          headAttribute1: \"Sample record header value\"\n"
            + "          ...\n"
            + "        key: |\n"
            + "          code: ${concat(\"UUID=\", RANDOM_UUID)}\n"
            + "          label: Product 1\n"
            + "        value: |\n"
            + "          attribute1: SingleWord\n"
            + "          attribute2: 2.0\n"
            + "          ...\n"
            + "      after: pause(100)\n"
            + "    - name: \"Name of Step 2\"\n"
            + "      ...\n"
            + "  afterAll: info(\"All steps are finished.\")\n"
            + "  ---\n"
            + "  name: NameOfSecondTestCase\n"
            + "  ...\n";
    static final String OPERATORS_DOC = BRIGHTYELLOW + "Operators/Tokens:" + WHITE + "\n"
            + "  -      -3       -3     Unary minus operator: negates the number value.\n"
            + "  +      4+3       7     Addition operator: adds two numbers.\n"
            + "  -      9-5       4     Subtraction operator; subtracts second number from first number.\n"
            + "  *      2*3       6     Multiplication operator: multiplies two numbers.\n"
            + "  /      5/2      2.5    Division operator: divides first number by second number.\n"
            + "  =     A=3.14           Assignment operator.\n"
            + "  (    3*(1+2)     9     Left brace: start increased priority.\n"
            + "  )     -(-4)      4     Right brace: ends increased priority.\n";
    static final String CONDITIONS_DOC = BRIGHTYELLOW + "Conditions/Tokens:" + WHITE + "\n"
            + "  ?    cnd?stm           Executes a statement only if condition is true (=1).\n"
            + "  ==   \"A\"==\"A\"    1     Equal: true if arguments are equals.\n"
            + "  !=     5!=5      0     Not Equal: true if arguments are differents.\n"
            + "  <=    2<=1+1     1     Lesser or Equal: true if left argument is smaller or equal to right argument.\n"
            + "  <      2<2       0     Lesser: true if left argument is strictly smaller than right argument.\n"
            + "  >=     0>=1      0     Greater or Equal: true if left argument is greater or equal to right argument.\n"
            + "  >      3>2       1     Greater: true if left argument is strictly greater than right argument.\n";
    static final String SPECIALS_DOC = BRIGHTYELLOW + "Specials/Tokens:" + WHITE + "\n"
            + "  ;                      Ends the current in-line statement an starts a new one.\n";

    @Inject
    private Context context;

    @Override
    public void run() {
        System.out.println(SAMPLE_CONFIG);
        System.out.println(SAMPLE_TEST_CASE);
        System.out.println(OPERATORS_DOC);
        System.out.println(CONDITIONS_DOC);
        System.out.println(SPECIALS_DOC);
        displayScriptFunctions();
    }

    private void displayScriptFunctions() {
        var maxCmd = 0;
        var maxPar = 0;
        var maxRes = 0;
        for (var f : context.functions()) {
            maxCmd = max(maxCmd, f.command().length());
            final var doc = f.doc();
            maxPar = max(maxPar, doc.param().length());
            maxRes = max(maxRes, doc.result().length());
        }
        System.out.println(BRIGHTYELLOW + "Scripting Functions:" + WHITE);
        FuncType previousType = null;
        for (var f : context.functions().stream().sorted(Comparator.comparing(o -> o.doc().type())).toList()) {
            if (f.doc().type() != previousType) {
                System.out.println(" " + f.doc().type() + ":");
                previousType = f.doc().type();
            }
            System.out.print("  " + f.command() + " ".repeat(maxCmd - f.command().length()));
            final var doc = f.doc();
            System.out.print('(' + doc.param() + ')' + " ".repeat(maxPar - f.doc().param().length() + 1));
            System.out.print(doc.result() + " ".repeat(maxRes - doc.result().length() + 1));
            System.out.println(doc.description());
        }
    }
}
