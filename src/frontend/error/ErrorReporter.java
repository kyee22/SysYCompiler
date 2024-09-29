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

import static utils.FileUtils.writeListToFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ErrorReporter implements ErrorListener {
    private List<ErrorMessage> errorMessages = new ArrayList<>();

    @Override
    public void onError(int lineno, ErrorType errorType) {
        errorMessages.add(new ErrorMessage(lineno, errorType));
    }

    @Override
    public void onRollback(int lineno, ErrorType errorType) {

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
}
