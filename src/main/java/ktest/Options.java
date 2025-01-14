package ktest;

import ktest.domain.config.PresetOptions;
import picocli.CommandLine;

final class Options extends PresetOptions {
    @CommandLine.Option(names = {"-e", "--env"}, description = "Name of the environment to use.", required = true)
    String env;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Path of the config file.", defaultValue = "ktconfig.yml")
    String config;

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path of test case description file to execute.", defaultValue = "ktestcase.yml")
    public String file;
}
