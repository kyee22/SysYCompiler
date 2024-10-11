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

package frontend.sysy.typesystem;

import java.util.List;

public class Type {
    private static Type INT = new Type(Kind.INT);
    private static Type CHAR = new Type(Kind.CHAR);
    private static Type CONST_INT = new Type(Kind.CONST_INT, true);
    private static Type CONST_CHAR = new Type(Kind.CONST_CHAR, true);
    private static Type INT_ARRAY = new Type(Kind.INT_ARRAY);
    private static Type CHAR_ARRAY = new Type(Kind.CHAR_ARRAY);
    private static Type CONST_INT_ARRAY = new Type(Kind.CONST_INT_ARRAY, true);
    private static Type CONST_CHAR_ARRAY = new Type(Kind.CONST_CHAR_ARRAY, true);
    private static Type VOID = new Type(Kind.VOID);

    private Kind kind;
    private boolean isConst = false;

    protected Type(Kind kind) {
        this.kind = kind;
    }

    protected Type(Kind kind, boolean isConst) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return kind.typeCode;
    }

    public static Type makeVoidType() {
        return VOID;
    }

    public static Type makeIntType() {
        return INT;
    }

    public static Type makeCharType() {
        return CHAR;
    }

    public static Type makeIntType(boolean isConst) {
        return isConst ? CONST_INT : INT;
    }

    public static Type makeCharType(boolean isConst) {
        return isConst ? CONST_CHAR : CHAR;
    }


    public static Type makeConstIntType() {
        return CONST_INT;
    }

    public static Type makeConstCharType() {
        return CONST_CHAR;
    }

    public static Type makeVoidFuncType(List<Type> paramTypes) {
        return new FuncType(Kind.VOID_FUNCTION, paramTypes);
    }

    public static Type makeIntFuncType(List<Type> paramTypes) {
        return new FuncType(Kind.INT_FUNCTION, paramTypes);
    }

    public static Type makeCharFuncType(List<Type> paramTypes) {
        return new FuncType(Kind.CHAR_FUNCTION, paramTypes);
    }

    public static Type makeIntArrayType() {
        return INT_ARRAY;
    }

    public static Type makeCharArrayType() {
        return CHAR_ARRAY;
    }

    public static Type makeConstIntArrayType() {
        return CONST_INT_ARRAY;
    }

    public static Type makeConstCharArrayType() {
        return CONST_CHAR_ARRAY;
    }


    protected enum Kind {
        VOID_FUNCTION("VoidFunc"),
        CHAR_FUNCTION("CharFunc"),
        INT_FUNCTION("IntFunc"),
        CONST_INT("ConstInt"),
        CONST_CHAR("ConstChar"),
        CONST_INT_ARRAY("ConstIntArray"),
        CONST_CHAR_ARRAY("ConstCharArray"),
        INT("Int"),
        CHAR("Char"),
        INT_ARRAY("IntArray"),
        CHAR_ARRAY("CharArray"),
        VOID("Void")
        ;

        private String typeCode;

        Kind(String typeCode) {
            this.typeCode = typeCode;
        }
    }

    public boolean isConst() {
        return kind == Kind.CONST_INT || kind == Kind.CONST_CHAR
                || kind == Kind.CONST_INT_ARRAY || kind == Kind.CONST_CHAR_ARRAY;
    }

    public static boolean match(Type paramType, Type argType) {
        if (paramType == null || argType == null) {
            return false;
        }
        switch (paramType.kind) {
            case INT:
            case CHAR:
                return argType.kind == Kind.INT || argType.kind == Kind.CHAR
                        || argType.kind == Kind.CONST_INT || argType.kind == Kind.CONST_CHAR;
            case INT_ARRAY:
                return argType.kind == Kind.INT_ARRAY;
            case CHAR_ARRAY:
                return argType.kind == Kind.CHAR_ARRAY;
            default:
                return false;
        }
    }

    public boolean isVoid() {return kind == Kind.VOID;}

    public boolean isInt() {return kind == Kind.INT;}

    public boolean isChar() {return kind == Kind.CHAR;}

    public boolean isConstInt() {return kind == Kind.CONST_INT;}

    public boolean isConstChar() {return kind == Kind.CONST_CHAR;}

    public boolean isIntArray() {return kind == Kind.INT_ARRAY;}

    public boolean isCharArray() {return kind == Kind.CHAR_ARRAY;}

    public boolean isVoidFunc() {return kind == Kind.VOID_FUNCTION;}

    public boolean isIntFunc() {return kind == Kind.INT_FUNCTION;}

    public boolean isCharFunc() {return kind == Kind.CHAR_FUNCTION;}
}
