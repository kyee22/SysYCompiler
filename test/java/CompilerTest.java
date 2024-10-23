/*
 * SysYCompiler: A Compiler for SysY.
 *
 * SysYCompiler is an individually developed course project
 * for Compiling Techniques @ School of Computer Science &
 * Engineering, Beihang University, Fall 2024.
 *
 * Copyright (C) 2024 Yixuan Kuang <kyee22@buaa.edu.cn>
 *
 * This file is part of SysYCompiler.
 */

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
    Path rootDir = Paths.get(".");
    Path outputPath;
    Path testCasePath;


    @Test
    public void testLexerPublic() throws IOException, InterruptedException {
        Compiler.LAB = 1;
        outputPath = Paths.get("lexer.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "lexer-public", "A");
        assertTrue(runBatchTests(testCasePath,  outputPath));

        testCasePath = Paths.get(".", "test", "python", "testcases", "lexer-public", "B");
        assertTrue(runBatchTests(testCasePath,  outputPath));

        testCasePath = Paths.get(".", "test", "python", "testcases", "lexer-public", "C");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }


    @Test
    public void testLexerErrorPrivate() throws IOException, InterruptedException {
        Compiler.LAB = 1;
        outputPath = Paths.get("error.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "lexer-error-private");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }

    @Test
    public void testParserPublic() throws IOException, InterruptedException {
        Compiler.LAB = 2;
        outputPath = Paths.get("parser.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "parser-public", "A");
        assertTrue(runBatchTests(testCasePath,  outputPath));

        testCasePath = Paths.get(".", "test", "python", "testcases", "parser-public", "B");
        assertTrue(runBatchTests(testCasePath,  outputPath));

        testCasePath = Paths.get(".", "test", "python", "testcases", "parser-public", "C");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }

    @Test
    public void testParserErrorPublic() throws IOException, InterruptedException {
        Compiler.LAB = 2;
        outputPath = Paths.get("error.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "parser-error-public");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }

    @Test
    public void testParserErrorPrivate() throws IOException, InterruptedException {
        Compiler.LAB = 2;
        outputPath = Paths.get("error.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "parser-error-private");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }

    @Test
    public void testSemanticCheckPublic() throws IOException, InterruptedException {
        Compiler.LAB = 3;
        outputPath = Paths.get("symbol.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "semantic-check-public", "A");
        assertTrue(runBatchTests(testCasePath,  outputPath));

        testCasePath = Paths.get(".", "test", "python", "testcases", "semantic-check-public", "B");
        assertTrue(runBatchTests(testCasePath,  outputPath));

        testCasePath = Paths.get(".", "test", "python", "testcases", "semantic-check-public", "C");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }

    @Test
    public void testSemanticCheckErrorPublic() throws IOException, InterruptedException {
        Compiler.LAB = 3;
        outputPath = Paths.get("error.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "semantic-check-error-public");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }

    @Test
    public void testSemanticCheckErrorPrivate() throws IOException, InterruptedException {
        Compiler.LAB = 3;
        outputPath = Paths.get("error.txt");

        testCasePath = Paths.get(".", "test", "python", "testcases", "semantic-check-error-private");
        assertTrue(runBatchTests(testCasePath,  outputPath));
    }



    public boolean compare(Path outputFile, Path expectedFile) throws IOException {
        List<String> outputLines = Files.readAllLines(outputFile);
        List<String> expectedLines = Files.readAllLines(expectedFile);

        int cnt = 0;

        if (outputLines.size() != expectedLines.size()) {
            System.out.println("Error " + cnt++ + ": expected " + expectedLines.size() + " lines but got " + outputLines.size() + " lines");
        }

        for (int i = 0; i < Math.min(outputLines.size(), expectedLines.size()); i++) {
            String outputLine = i < outputLines.size() ? outputLines.get(i) : null;
            String expectedLine = i < expectedLines.size() ? expectedLines.get(i) : null;
            if (!outputLine.equals(expectedLine)) {
                System.out.println("Error " + cnt++ + ": expected \"" + expectedLine + "\" but got \"" + outputLine + "\" at line " + (i + 1));
            }
        }

        return cnt == 0;
    }

    public boolean runBatchTests(Path batchDirectory, Path outputPath) throws IOException, InterruptedException {
        List<Path> testcases = Files.list(batchDirectory)
                .filter(Files::isDirectory)
                .filter(dir -> !dir.equals(batchDirectory))
                .sorted()
                .collect(Collectors.toList());

        printColoredText("Run test cases in " + batchDirectory + " ...", ANSI_PURPLE);
        int numPass = 0, numFail = 0;
        for (Path testcase : testcases) {
            if (runTestCase(testcase, outputPath)) {
                printColoredText("[       OK ] TEST " + testcase.getFileName(), ANSI_GREEN);
                ++numPass;
            } else {
                printColoredText("[   FAILED ] TEST " + testcase.getFileName(), ANSI_RED);
                ++numFail;
            }
        }
        printColoredText("", ANSI_PURPLE);
        printColoredText("[  PASSED  ] " + numPass +  " tests.\n", ANSI_GREEN);
        if (numFail != 0) {
            printColoredText("[   FAILED ] " + numFail +  " tests.\n", ANSI_RED);
        }
        return numFail == 0;
    }

    public boolean runTestCase(Path testcaseDirectory, Path outputPath) throws IOException, InterruptedException {
        Path testfilePath = testcaseDirectory.resolve("testfile.txt");
        Path ansPath = testcaseDirectory.resolve("ans.txt");

        // Copy testfile to project root
        Files.copy(testfilePath, rootDir.resolve("testfile.txt"), StandardCopyOption.REPLACE_EXISTING);

        // Run the Java compiler
        Compiler.main(null);

        // Compare
        return compare(outputPath, ansPath);
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private static void printColoredText(String text, String colorCode) {
        System.out.println(colorCode + text + ANSI_RESET);
    }
}