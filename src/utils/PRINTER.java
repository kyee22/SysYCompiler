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


public class PRINTER {

    // ANSI转义序列
    private static final String ESC = "\u001B["; // ESC 字符
    private static final String RESET = ESC + "0m"; // 重置所有格式
    private static final String UNDERLINE = ESC + "4m"; // 下划线

    // 颜色
    private static final String BRIGHT_CYAN = ESC + "96m";
    private static final String BRIGHT_RED = ESC + "91m";
    private static final String MAGENTA = ESC + "35m";
    private static final String BRIGHT_YELLOW = ESC + "93m";
    private static final String WHITE = ESC + "97m";
    private static final String BRIGHT_MAGENTA = ESC + "95m";
    private static final String BRIGHT_GREEN = ESC + "92m";
    private static final String BRIGHT_BLUE = ESC + "94m";

    // 根据枚举值返回对应的ANSI颜色代码
    private static String getColorCode(Color color) {
        switch (color) {
            case BRIGHT_CYAN:
                return BRIGHT_CYAN;
            case BRIGHT_RED:
                return BRIGHT_RED;
            case MAGENTA:
                return MAGENTA;
            case BRIGHT_YELLOW:
                return BRIGHT_YELLOW;
            case WHITE:
                return WHITE;
            case BRIGHT_MAGENTA:
                return BRIGHT_MAGENTA;
            case BRIGHT_GREEN:
                return BRIGHT_GREEN;
            case BRIGHT_BLUE:
                return BRIGHT_BLUE;
            default:
                return RESET;
        }
    }

    // 打印带颜色和下划线的文本
    public static void print(String message, Color color, boolean underline) {
        String colorCode = getColorCode(color);
        String underlineCode = underline ? UNDERLINE : "";
        String formattedMessage = colorCode + underlineCode + message + RESET;
        System.out.print(formattedMessage);
    }

    // 打印带颜色和下划线的文本，后加换行符
    public static void println(String message, Color color, boolean underline) {
        String colorCode = getColorCode(color);
        String underlineCode = underline ? UNDERLINE : "";
        String formattedMessage = colorCode + underlineCode + message + RESET;
        System.out.println(formattedMessage);
    }

    // 打印带颜色的文本（无下划线）
    public static void print(String message, Color color) {
        print(message, color, false);
    }

    // 打印带颜色的文本，后加换行符（无下划线）
    public static void println(String message, Color color) {
        println(message, color, false);
    }

    // 打印普通文本（无颜色，无下划线）
    public static void print(String message) {
        System.out.print(message);
        //print(message, Color.WHITE, false);   // GPT Wrong!!
    }

    // 打印普通文本，后加换行符（无颜色，无下划线）
    public static void println(String message) {
        System.out.println(message);
        //println(message, Color.WHITE, false); // GPT Wrong!!
    }

    public static void printIndentaion(int level) {
        for (int i = 0; i < level; ++i) {
            print("    ");
        }
    }

    public static void printWhiteSpace(int level) {
        for (int i = 0; i < level; ++i) {
            print(" ");
        }
    }

}
