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

public class DEBUG {
    public static boolean debug = false;
    private static int ident = 0;

    public static void println(String s) {
        if (debug) {
            for (int i = 0; i < ident; ++i) {
                System.out.print(" ");
            }
            System.out.println(s);
        }
    }

    public static void print(String s) {
        if (debug) {
            System.out.print(s);
        }
    }

    public static void lprintln(String s) {
        if (debug) {
            for (int i = 0;  i < ident; ++i) {
                System.out.print(" ");
            }
            System.out.println(s);
            ident += 4;
        }
    }

    public static void lprint(String s) {
        if (debug) {
            for (int i = 0;  i < ident; ++i) {
                System.out.print(" ");
            }
            System.out.print(s);
            ident += 4;
        }
    }


    public static void rprintln(String s) {
        if (debug) {
            ident -= 4;
            for (int i = 0;  i < ident; ++i) {
                System.out.print(" ");
            }
            System.out.println(s);
        }
    }

}
