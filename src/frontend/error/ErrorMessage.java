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

public class ErrorMessage {
        private int lineno;
        private ErrorType type;

        public ErrorMessage(int lineno, ErrorType type) {
            this.lineno = lineno;
            this.type = type;
        }

    public ErrorType getType() {
        return type;
    }

    public int getLineno() {
        return lineno;
    }

    @Override
        public String toString() {
            return lineno + " " + type.toString();
        }
    }
