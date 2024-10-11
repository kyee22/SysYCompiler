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

package frontend.error;

import utils.PRINTER;

import static utils.Color.*;
import static utils.FileUtils.readLines;
import static utils.FileUtils.writeListToFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ErrorReporter implements ErrorListener {
    private List<ErrorMessage> errorMessages = new ArrayList<>();
    private List<String> lines = new ArrayList<>();
    private String inputPath = null;

    @Override
    public void onError(int lineno, int colno, ErrorType errorType) {
        errorMessages.add(new ErrorMessage(lineno, colno, errorType));
    }


    @Override
    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    @Override
    public void flushErrors(String errorFilePath) {
        if (errorMessages.isEmpty()) {
            return;
        }
        // 对于错误的源程序，将 错误所在的行号和错误的类别码
        // 按行号从小到大输出至 error.txt，行号从1开始编号。
        Collections.sort(errorMessages, Comparator.comparingInt(e -> e.getLineno()));
        writeListToFile(errorMessages, errorFilePath);
    }

    public void read(String filePath) {
        List<String> lines = readLines(filePath);
        inputPath = filePath;
        this.lines.add(null);       // index 0
        this.lines.addAll(lines);   // start from index 1
    }

    public void hint() {
        if (errorMessages.isEmpty()) {
            PRINTER.println("Your program has passed semantic check!", BRIGHT_BLUE);
            return;
        }
        // 对于错误的源程序，将 错误所在的行号和错误的类别码
        // 按行号从小到大输出至 error.txt，行号从1开始编号。
        Collections.sort(errorMessages, Comparator.comparingInt(e -> e.getLineno()));
        for (ErrorMessage errorMessage : errorMessages) {
            switch (errorMessage.getType()) {
                case ILLEGAL_SYM:
                    lexerHint(errorMessage);
                    break;
                case MISSING_SEMICOLON:
                case MISSING_RPARENT:
                case MISSING_RBRACK:
                    parserHint(errorMessage);
                    break;
                default:
                    break;
            }
        }
    }

    private void lexerHint(ErrorMessage errorMessage) {
        String sym = lines.get(errorMessage.getLineno()).substring(errorMessage.getColno() - 1, errorMessage.getColno());
        String expected = switch (sym) {
            case "&" -> "&&";
            case "|" -> "||";
            default -> "";
        };
        System.out.print(inputPath + ":" + errorMessage.getLineno() + ":" + errorMessage.getColno() + ": ");
        PRINTER.print("Error: ", BRIGHT_RED);
        System.out.println("illegal token: '" + sym + "'; did you mean '" + expected + "'?");
        System.out.println(lines.get(errorMessage.getLineno()));
        PRINTER.printWhiteSpace(errorMessage.getColno() - 1);
        PRINTER.println("~", BRIGHT_RED);
        PRINTER.printWhiteSpace(errorMessage.getColno() - 1);
        PRINTER.println(expected, BRIGHT_GREEN);
    }

    private void parserHint(ErrorMessage errorMessage) {
        System.out.print(inputPath + ":" + errorMessage.getLineno() + ":" + errorMessage.getColno() + ": ");
        PRINTER.print("Error: ", BRIGHT_RED);
        String expected = switch (errorMessage.getType()) {
            case MISSING_SEMICOLON -> ";";
            case MISSING_RPARENT -> ")";
            case MISSING_RBRACK -> "]";
            default -> "";
        };
        System.out.println("expected '" + expected + "'");

        System.out.println(lines.get(errorMessage.getLineno()));
        PRINTER.printWhiteSpace(errorMessage.getColno() - 1);
        PRINTER.println("^", BRIGHT_RED);
        PRINTER.printWhiteSpace(errorMessage.getColno() - 1);
        PRINTER.println(expected, BRIGHT_GREEN);
    }
}
