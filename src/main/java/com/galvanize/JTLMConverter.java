package com.galvanize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Java to Learn Markdown Converter (JTlMConverter) converts Java files (or Markdown files with
 * references to Java files) into valid syntax for Learn MD to be able to run Java code snippets
 * with corresponding tests.
 */
public class JTLMConverter {

    /**
     * INSERT_SYNTAX refers to {@value}.  When a line starts with {@value} in a given .md file,
     * JLTMConverter will refer to the line's contents as:
     * <p>
     * {@value} <SourceFilePath> <TestFilePath>
     */
    public static final String INSERT_SYNTAX = "<insert>";
    private static final String UID_FIELD = "UID:";

    static String replaceInsertWithJavaMD(File markdownSource) throws IOException {
        Scanner inputMarkdown = new Scanner(markdownSource);

        String nextLine = inputMarkdown.nextLine();
        StringBuilder result = new StringBuilder();
        while (inputMarkdown.hasNextLine()) {
            if (nextLine.startsWith(UID_FIELD)) {
                nextLine = String.format("%s %s", UID_FIELD, UUID.randomUUID());
            }

            if (nextLine.startsWith(INSERT_SYNTAX)) {
                String[] split = nextLine.split("\\s+");
                appendWithNewLine(result,
                        generateLearnMDFor(new File(split[1]), new File(split[2])));
            } else {
                appendWithNewLine(result, nextLine);
            }
            nextLine = inputMarkdown.nextLine();
        }
        return result.toString();
    }

    static String generateLearnMDFor(File sourceFile, File testFile) throws IOException {
        StringBuilder result = new StringBuilder();
        Scanner testFileScanner = new Scanner(testFile);

        Set<String> imports = new TreeSet<>();


        addImportsFromTo(testFileScanner, testFile.getName(), imports);

        Scanner sourceFileScanner = new Scanner(sourceFile);
        addImportsFromTo(sourceFileScanner, sourceFile.getName(), imports);

        appendWithNewLine(result, "##### !setup");
        appendWithNewLine(result, "```java");

        for (String s : imports) {
            appendWithNewLine(result, s);
        }

        result.append(System.lineSeparator());

        appendWithNewLine(result, String.format("class %s {", sourceFile.getName().split("\\.")[0]));

        appendWithNewLine(result, "```");
        appendWithNewLine(result, "##### !end-setup");

        appendWithNewLine(result, "##### !placeholder");
        appendWithNewLine(result, "```java");
        // parse solution print out until exclude start section
        // find exclude end and resume printing, don't print class end }
        printPlaceHolder(result, sourceFileScanner);
        appendWithNewLine(result, "```");

        appendWithNewLine(result, "##### !end-placeholder");
        appendWithNewLine(result, "##### !tests");

        appendWithNewLine(result, "```java");
        appendWithNewLine(result, "}");  // WHY?
        result.append(System.lineSeparator());
        appendWithNewLine(result, "public class SnippetTest {");
        // print rest of test file

        while (testFileScanner.hasNextLine()) {
            appendWithNewLine(result, testFileScanner.nextLine());
        }
        appendWithNewLine(result, "```");
        appendWithNewLine(result, "##### !end-tests");
        return result.toString();
    }

    private static void appendWithNewLine(StringBuilder stringBuilder, String s) {
        stringBuilder.append(s);
        stringBuilder.append(System.lineSeparator());
    }

    private static void printPlaceHolder(StringBuilder result, Scanner sourceFile) {
        List<String> lines = new ArrayList<>();

        int firstIndentAmount = 0;
        while (sourceFile.hasNextLine()) {
            String line = sourceFile.nextLine();
            if (!line.isBlank() && firstIndentAmount == 0) {
                while (Character.isWhitespace(line.charAt(firstIndentAmount))) {
                    firstIndentAmount++;
                }
            }
            lines.add(line);
        }

        int lineNumberToStopExclusive = lines.size() - 1;
        while (!lines.get(lineNumberToStopExclusive).matches(".*}.*")) {
            lineNumberToStopExclusive--;
        }

        boolean exclude = false;
        for (int i = 0; i < lineNumberToStopExclusive; i++) {
            String line = lines.get(i);
            if (line.matches("\\s*//\\s*EXCLUDE-START.*")) {
                exclude = true;
            } else if (line.matches("\\s*//\\s*EXCLUDE-END.*")) {
                exclude = false;
                continue;
            }

            if (!exclude) {
                appendWithNewLine(result, line.substring(firstIndentAmount));
            }
        }
    }

    private static void addImportsFromTo(Scanner testFile, String testFilePath, Set<String> imports) {
        while (true) {
            String nextLine = testFile.nextLine();
            String[] filePath = testFilePath.split("\\.")[0].split("/");
            if (nextLine.contains("class") && nextLine.contains(filePath[filePath.length - 1])) {
                break;
            } else if (nextLine.contains("import")
                    && !nextLine.contains("junit")
                    && !imports.contains(nextLine)) {
                imports.add(nextLine);
            }
        }
    }
}
