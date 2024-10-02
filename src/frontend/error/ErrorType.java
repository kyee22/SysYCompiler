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

public enum ErrorType {
    /*  词法分析中会出现的错误 */
    ILLEGAL_SYM("a"),                   // 非法符号         TODO

    /*  语法分析中会出现的错误 */
    MISSING_SEMICOLON("i"),             // 缺少分号
    MISSING_RPARENT("j"),               // 缺少右小括号 ')'
    MISSING_RBRACK("k"),                // 缺少右中括号 ']'

    /*  语义分析中会出现的错误 */
    REDEF_SYM("b"),                     // 重定义符号        TODO
    UNDEF_SYM("c"),                     // 未定义符号        TODO
    PARAM_COUNT_MISMATCH("d"),          // 函数参数个数不匹配  TODO
    PARAM_TYPE_MISMATCH("e"),           // 函数参数类型不匹配  TODO
    RETURN_MISMATCH_VOID("f"),          // 无返回值的函数存在不匹配的return语句
    RETURN_MISSING("g"),                // 有返回值的函数缺少return语句
    CONST_ASSIGN_ERROR("h"),            // 不能改变常量的值     TODO

    FORMAT_COUNT_MISMATCH("l"),         // printf中格式字符与表达式个数不匹配
    BREAK_CONTINUE_OUTSIDE_LOOP("m");   // 在非循环块中使用break和continue语句
    ;

    private String errorCode;

    ErrorType(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return errorCode;
    }
}
