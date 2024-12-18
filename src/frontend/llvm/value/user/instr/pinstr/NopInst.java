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

package frontend.llvm.value.user.instr.pinstr;

import com.sun.jdi.VoidType;
import frontend.llvm.type.Type;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.Instruction;

import java.util.List;
import java.util.Optional;

/**
 *  伪指令 (LLVM IR 种没有 nop 指令)
 *  设立这个伪指令的动机是作为 CFG<Instruction> 的 entry 和 exit 节点
 */

public class NopInst extends Instruction {
    private NopInst() {
        super(null, OpID.PSEUDO_NOP, null);
    }

    public static NopInst create() {
        return new NopInst();
    }

    @Override
    public String print() {
        return "nop";
    }

    /******************** 活跃变量分析时用到的 api ********************/
    @Override
    public Optional<Value> getDef() {
        return Optional.empty();
    }

    @Override
    public List<Value> getUses() {
        return List.of();
    }
}
