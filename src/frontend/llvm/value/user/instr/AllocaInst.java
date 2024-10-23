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

import frontend.llvm.type.PointerType;
import frontend.llvm.type.Type;
import frontend.llvm.value.BasicBlock;

import java.util.Set;

public class AllocaInst extends Instruction {
    private static Set<Type.TypeID> allowedTypes = Set.of(Type.TypeID.IntegerTyID, Type.TypeID.FloatTyID,
            Type.TypeID.ArrayTyID, Type.TypeID.PointerTyID);

    private AllocaInst(Type ty, BasicBlock bb) {
        super(PointerType.get(ty), OpID.ALLOCA, bb);
        if (!allowedTypes.contains(ty.getTypeId())) {
            throw new IllegalArgumentException("Not allowed type for alloca");
        }
    }

    public static AllocaInst createAlloca(Type ty, BasicBlock bb) {
        return new AllocaInst(ty, bb);
    }

    private Type getAllocaType() {
        return getType().getPointerElementType();
    }

    @Override
    public String print() {
        StringBuilder instrIr = new StringBuilder();

        instrIr.append("%")
                .append(getName())
                .append(" = ")
                .append(getInstrOpName())
                .append(" ")
                .append(getAllocaType().print());

        return instrIr.toString();
    }
}
