package fr.dcotton.ktest;

import fr.dcotton.ktest.script.Context;
import jakarta.inject.Inject;
import picocli.CommandLine;

import static fr.dcotton.ktest.MainCommand.VERSION;
import static java.lang.Math.max;

@CommandLine.Command(name = "doc", description = "Display full documentation.",
        mixinStandardHelpOptions = true, version = VERSION)
public class DocCommand implements Runnable {
    @Inject
    private Context context;

    @Override
    public void run() {
        var maxCmd = 0;
        var maxPar = 0;
        var maxRes = 0;
        for (var f : context.functions()) {
            maxCmd = max(maxCmd, f.command().length());
            final var doc = f.doc();
            maxPar = max(maxPar, doc.param().length());
            maxRes = max(maxRes, doc.result().length());
        }
        System.out.println("Script Functions:");
        for (var f : context.functions()) {
            System.out.print("  " + f.command() + " ".repeat(maxCmd - f.command().length()));
            final var doc = f.doc();
            System.out.print('(' + doc.param() + ')' + " ".repeat(maxPar - f.doc().param().length() + 1));
            System.out.print(doc.result() + " ".repeat(maxRes - doc.result().length() + 1));
            System.out.println(doc.description());
        }
    }
}