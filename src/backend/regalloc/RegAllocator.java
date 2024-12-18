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

package backend.regalloc;

import backend.mips.Register;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;

import java.util.List;
import java.util.Map;

public interface RegAllocator {
    public Map<Value, Register> allocate(Function function);

    public List<Register> getUsedSavadRegs();
}
