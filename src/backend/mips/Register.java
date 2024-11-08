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

package backend.mips;

import java.util.Arrays;
import java.util.List;

public enum Register {
    /*
            REGISTER	NAME        USAGE
            $0	        $zero	    常量0
            $1	        $at	        保留给汇编器
            $2-$3	    $v0-$v1	    函数调用返回值
            $4-$7	    $a0-$a3	    函数调用参数寄存器，从左到右依次分配，未取得寄存器的实参（>4的参数）使用运行栈传参
            $8-$15	    $t0-$t7	    临时寄存器
            $16-$23     $s0-$s7	    全局寄存器，分配给局部变量和形参
            $24-$25	    $t8-$t9	    临时寄存器
            $28	        $gp	        全局指针(Global Pointer)
            $29	        $sp	        堆栈指针(Stack Pointer)
            $30	        $fp	        帧指针(Frame Pointer)
            $31	        $ra	        返回地址(return address)
     */

    // 通用寄存器
    REG_ZERO("$zero"), // 始终为0
    REG_AT("$at"),     // 供汇编器使用
    REG_V0("$v0"),     REG_V1("$v1"), // 返回值寄存器
    REG_A0("$a0"),     REG_A1("$a1"),     REG_A2("$a2"),     REG_A3("$a3"), // 参数寄存器
    REG_T0("$t0"),     REG_T1("$t1"),     REG_T2("$t2"),     REG_T3("$t3"),
    REG_T4("$t4"),     REG_T5("$t5"),     REG_T6("$t6"),     REG_T7("$t7"), // 临时寄存器
    REG_S0("$s0"),     REG_S1("$s1"),     REG_S2("$s2"),     REG_S3("$s3"),
    REG_S4("$s4"),     REG_S5("$s5"),     REG_S6("$s6"),     REG_S7("$s7"), // 保存寄存器
    REG_T8("$t8"),     REG_T9("$t9"),     REG_K0("$k0"),     REG_K1("$k1"), // 特殊用途寄存器
    REG_GP("$gp"),     REG_SP("$sp"),     REG_FP("$fp"),     REG_RA("$ra");  // 特殊寄存器

    private final String name;

    Register(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Register fromString(String name) {
        for (Register reg : values()) {
            if (reg.getName().equals(name)) {
                return reg;
            }
        }
        throw new IllegalArgumentException("Unknown register: " + name);
    }
}
