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

package utils;

import static utils.FileUtils.*;

public class Charstream {
    public static final char EOF = (char) -1;

    private String string;
    private int pos = 0;

    private Charstream(String string) {
        this.string = string;
    }

    public static Charstream fromFile(String filePath) {
        return new Charstream(readContentsAsString(filePath));
    }

    public static Charstream fromString(String string) {
        return new Charstream(string);
    }

    public char getc() {
        if (pos >= string.length()) {
            ++pos;
            return EOF;
        }
        return string.charAt(pos++);
    }

    public void ungetc() {
        --pos;
    }
}
