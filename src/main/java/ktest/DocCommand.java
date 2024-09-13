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

@CommandLine.Command(name = "doc", description = "Display full documentation.",
        mixinStandardHelpOptions = true, version = VERSION)
public class DocCommand implements Runnable {
    final static String SAMPLE_CONFIG = STR."""
\{BRIGHTYELLOW}Sample ktconfig.yml:\{WHITE}
  registries:
    - name: pi_registry
      url: http://192.168.0.105:8081
      user: UserName
      password: UserPassword
    - name: registry_2
      ...
  brokers:
    - name: pi_broker
      bootstrap.servers: 192.168.0.105:9092
      registry: pi_registry
      sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username="USER" password="PASSWORD";
      sasl.mechanism: PLAIN
      security.protocol: SASL_SSL
      group.id: pi.ktest-group
    - name: local_broker
      ...
  environments:
    - name: pi
      onStart: |
        BROKER_USED="pi_broker"
        ...
      onEnd: info("Test finished")
    - name: dev
      ...
""";
    final static String SAMPLE_TEST_CASE = STR."""
\{BRIGHTYELLOW}Sample Test Case file:\{WHITE}
  name: Name of the Test Case
  beforeAll: |
    TIMESTAMP = now()
    ...
  steps:
    - name: Name of Step 1
      before: RANDOM_UUID = uuid()
      broker: ktconfig_broker_name
      topic: DestinationTopicName
      keySerde: STRING
      valueSerde: AVRO
      action: SEND
      record:
        headers:
          headAttribute1: "Sample record header value"
          ...
        key: |
          code: P1
          label: Product 1
        value: |
          attribute1: SingleWord
          attribute2: 2.0
          ...
      after: pause(100)
    - name: Name of Step 2
      ...
  afterAll: info("All steps are finished.")
""";
    final static String OPERATORS_DOC = STR."""
\{BRIGHTYELLOW}Operators/Tokens:\{WHITE}
  -      -3       -3     Unary minus operator: negates the number value.
  +      4+3       7     Addition operator: adds to numbers.
  -      9-5       4     Subtraction operator; subtracts second number from first number.
  *      2*3       6     Multiplication operator: multiplies two numbers.
  /      5/2      2.5    Division operator: divides first number by second number.
  =     A=3.14           Assignment operator.
  (    3*(1+2)     9     Left brace: start increased priority.
  )     -(-4)      4     Right brace: ends increased priority.
""";

    @Inject
    private Context context;

    @Override
    public void run() {
        System.out.println(SAMPLE_CONFIG);
        System.out.println(SAMPLE_TEST_CASE);
        System.out.println(OPERATORS_DOC);
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
        System.out.println(STR."\{BRIGHTYELLOW}Script Functions:\{WHITE}");
        FuncType previousType = null;
        for (var f : context.functions().stream().sorted(Comparator.comparing(o -> o.doc().type())).toList()) {
            if (f.doc().type() != previousType) {
                System.out.println(STR." \{f.doc().type()}:");
                previousType = f.doc().type();
            }
            System.out.print(STR."  \{f.command()}\{" ".repeat(maxCmd - f.command().length())}");
            final var doc = f.doc();
            System.out.print('(' + doc.param() + ')' + " ".repeat(maxPar - f.doc().param().length() + 1));
            System.out.print(doc.result() + " ".repeat(maxRes - doc.result().length() + 1));
            System.out.println(doc.description());
        }
    }
}
