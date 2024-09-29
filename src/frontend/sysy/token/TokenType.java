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

public enum TokenType {
    /*      terminals       */
    IDENFR("IDENFR"),
    INTCON("INTCON"),
    STRCON("STRCON"),
    CHRCON("CHRCON"),

    /*      keywords       */
    MAINTK("MAINTK"),
    CONSTTK("CONSTTK"),
    INTTK("INTTK"),
    CHARTK("CHARTK"),
    BREAKTK("BREAKTK"),
    CONTINUETK("CONTINUETK"),
    IFTK("IFTK"),
    ELSETK("ELSETK"),
    FORTK("FORTK"),
    RETURNTK("RETURNTK"),
    VOIDTK("VOIDTK"),

    /*      i/o interfaces (viewed as keywords)       */
    GETINTTK("GETINTTK"),
    GETCHARTK("GETCHARTK"),
    PRINTFTK("PRINTFTK"),

    /*      logical operators       */
    NOT("NOT"),
    AND("AND"),
    OR("OR"),

    /*      arithmetical operators       */
    PLUS("PLUS"),
    MINU("MINU"),
    MULT("MULT"),
    DIV("DIV"),
    MOD("MOD"),

    /*      relational operators       */
    LSS("LSS"),
    LEQ("LEQ"),
    GRE("GRE"),
    GEQ("GEQ"),
    EQL("EQL"),
    NEQ("NEQ"),

    /*      others       */
    ASSIGN("ASSIGN"),
    SEMICN("SEMICN"),
    COMMA("COMMA"),
    LPARENT("LPARENT"),
    RPARENT("RPARENT"),
    LBRACK("LBRACK"),
    RBRACK("RBRACK"),
    LBRACE("LBRACE"),
    RBRACE("RBRACE"),

    /*      undefined, i.e. illegal input      */
    UNDEF("UNDEF"),

    /*      E.O.F      */
    EOFTK("EOFTK")
    ;

    private String typeCode;

    TokenType(String typeCode) {
        this.typeCode = typeCode;
    }

    @Override
    public String toString() {
        return typeCode;
    }
}
