package com.galvanize;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.Callable;

@Command(name = "java-to-learn", mixinStandardHelpOptions = true, version = "java-to-learn 0.1",
        description = "Converts Java files to valid Learn Markdown")
public class JTLMCLI implements Callable<Integer> {

    @Option(names = {"-c", "--clipboard"}, description = "Copies to clipboard (default: print to console)")
    private boolean clipboard;

    @Option(names = {"-j", "--java"},
            description = "The java source file path first and then the java test file path second. " +
                    "You must include -j and its values or -md and its values.",
            arity = "2")
    private File[] javaSourceAndTestPath;

    @Option(names = {"-md", "--markdown"},
            description = "The markdown source file path first and then markdown generated file path second." +
                    "You must include -j and its values or -md and its values.",
            arity = "2")
    private File[] markdownSourceAndResultPath;

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.


    // TODO: implement adding learn preview into this
    public static void main(String... args) {
        int exitCode = new CommandLine(new JTLMCLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        String result;
        if (javaSourceAndTestPath != null) {
            result = JTLMConverter.generateLearnMDFor(javaSourceAndTestPath[0], javaSourceAndTestPath[1]);
        } else {
            result = JTLMConverter.replaceInsertWithJavaMD(markdownSourceAndResultPath[0]);
            PrintStream output = new PrintStream(markdownSourceAndResultPath[1]);
            output.print(result);
            System.out.println(String.format("Generated file at %s", markdownSourceAndResultPath[1]));
        }

        if (clipboard) {
            setClipboard(result);
        } else {
            System.out.println(result);
        }

        return 0;
    }

    private static void setClipboard(String result) {
        StringSelection selection = new StringSelection(result);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        System.out.println("The Learn Markdown has been copied to your clipboard.");
    }
}
