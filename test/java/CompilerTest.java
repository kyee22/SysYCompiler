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
    Path testCaseDir = Paths.get(".", "test", "python", "testcases");

    @Test
    public void test() throws IOException, InterruptedException {

        assertTrue(runBatchTests(Paths.get(".", "test", "python", "testcases", "parser-error-public"), Paths.get("error.txt")));
    }



    public boolean compare(Path outputFile, Path expectedFile) throws IOException {
        List<String> outputLines = Files.readAllLines(outputFile);
        List<String> expectedLines = Files.readAllLines(expectedFile);

        int cnt = 0;

        if (outputLines.size() != expectedLines.size()) {
            System.out.println("Error " + cnt++ + ": expected " + expectedLines.size() + " lines but got " + outputLines.size());
        }

        for (int i = 0; i < Math.max(outputLines.size(), expectedLines.size()); i++) {
            String outputLine = i < outputLines.size() ? outputLines.get(i) : null;
            String expectedLine = i < expectedLines.size() ? expectedLines.get(i) : null;
            if (!outputLine.equals(expectedLine)) {
                System.out.println("Error " + cnt++ + ": expected " + expectedLine + " lines but got " + outputLine + " at line " + i + 1);
            }
        }

        return cnt == 0;
    }

    public boolean runBatchTests(Path batchDirectory, Path outputPath) throws IOException, InterruptedException {
        List<Path> testcases = Files.walk(batchDirectory)
                .filter(Files::isDirectory)
                .filter(dir -> !dir.equals(batchDirectory))
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Run test cases in " + batchDirectory + " ...");
        int numPass = 0, numFail = 0;
        for (Path testcase : testcases) {
            if (runTestCase(testcase, outputPath)) {
                System.out.println("[       OK ] TEST " + testcase);
                ++numPass;
            } else {
                System.out.println("[   FAILED ] TEST " + testcase);
                ++numFail;
            }
        }
        System.out.println("[  PASSED  ] " + numPass +  " tests.\n");
        if (numFail != 0) {
            System.out.println("[   FAILED ] " + numFail +  " tests.\n");
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
}