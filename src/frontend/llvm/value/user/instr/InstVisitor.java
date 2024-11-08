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

package frontend.llvm.value.user.instr;

import frontend.llvm.value.user.instr.Instruction;
import utils.DEBUG;

public interface InstVisitor<T> {
    default T visit(AllocaInst inst) {return visitDefault(inst);}
    default T visit(BranchInst inst) {return visitDefault(inst);}
    default T visit(CallInstr inst) {return visitDefault(inst);}
    default T visit(CastInst inst) {return visitDefault(inst);}
    default T visit(FBinaryInst inst) {return visitDefault(inst);}
    default T visit(FCmpInst inst) {return visitDefault(inst);}
    default T visit(GetElementPtrInst inst) {return visitDefault(inst);}
    default T visit(IBinaryInst inst) {return visitDefault(inst);}
    default T visit(ICmpInst inst) {return visitDefault(inst);}
    default T visit(LoadInst inst) {return visitDefault(inst);}
    default T visit(ReturnInst inst) {return visitDefault(inst);}
    default T visit(StoreInst inst) {return visitDefault(inst);}
    default T visit(Instruction inst) {return visitDefault(inst);}
    default T visitDefault(Instruction inst) {
        return null;
    }
}
