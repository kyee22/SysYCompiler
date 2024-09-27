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

public class ObjUtils {
    public static boolean any(Object a, Object... others) {
        for (Object b : others) {
            if (a.equals(b)) return true;
        }
        return false;
    }
}
