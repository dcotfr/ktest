package ktest;

import io.quarkiverse.mcp.server.stdio.runtime.StdioMcpMessageHandler;
import io.quarkus.runtime.Quarkus;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.io.OutputStream;
import java.io.PrintStream;

import static ktest.MainCommand.VERSION;

@CommandLine.Command(name = "mcp", description = "Start as MCP tool server.",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class McpCommand implements Runnable {
    @CommandLine.Option(names = {"-c", "--config"}, description = "Path of the config file.", defaultValue = "ktconfig.yml")
    private String config;

    @Inject
    private StdioMcpMessageHandler mcpServer;

    @Inject
    private McpMode mcpMode;

    @Override
    public void run() {
        mcpMode.enable();
        final var stdout = System.out;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            mcpServer.initialize(stdout);
            Quarkus.waitForExit();
        } finally {
            System.setOut(stdout);
        }
    }
}
