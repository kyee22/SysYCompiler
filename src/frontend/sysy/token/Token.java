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

package frontend.sysy.token;

public class Token {
    private TokenType type;
    private String text;
    private int lineno;
    private int colno;

    private Token(TokenType type, String text, int lineno, int colno) {
        this.type = type;
        this.text = text;
        this.lineno = lineno;
        this.colno = colno;
    }

    public static Token makeToken(TokenType type, String text, int lineno, int colno) {
        return new Token(type, text, lineno, colno);
    }

    @Override
    public String toString() {
        return type.toString() + " " + text;
        //return type.toString() + " " + text + " @L" + lineno + ":" + colno;
    }

    public TokenType getType() {
        return type;
    }

    public int getLineno() {
        return lineno;
    }

    public int getColno() {
        return colno;
    }

    public String getText() {
        return text;
    }

    public boolean is(TokenType type) {
        return this.type == type;
    }

    public boolean any(TokenType ...tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            if (tokenType == type) {
                return true;
            }
        }
        return false;
    }
}

