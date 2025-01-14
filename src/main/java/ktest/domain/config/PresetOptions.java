package ktest.domain.config;

import picocli.CommandLine;

public class PresetOptions {
    @CommandLine.Option(names = {"-b", "--back"}, description = "Back offset.", defaultValue = "250")
    public Integer backOffset;

    @CommandLine.Option(names = {"-m", "--matrix"}, description = "Path of the matrix summary file (xlsx format).", defaultValue = "ktmatrix.xlsx")
    public String matrix;

    @CommandLine.Option(names = {"-r", "--report"}, description = "Path of the test report file (JUnit format).", defaultValue = "ktreport.xml")
    public String report;

    @CommandLine.Option(names = {"-t", "--tags"}, description = "Tags to filter test cases to run.")
    public String tags;
}
