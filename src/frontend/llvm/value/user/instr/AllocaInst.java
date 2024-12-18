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
import frontend.llvm.value.Use;
import frontend.llvm.value.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Type getAllocaType() {
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

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}

    public boolean isPromotable() {
        // 关于这个 API, LLVM IR 官方有更详尽的约束, 这里做简化处理:
        // 对基本量的 alloca 是 promotable 的, 对数组的 alloca 是不 promotable 的.
        for (Use use : useList) {
            if (use.getUser() instanceof GetElementPtrInst) {
                return false;
            }
        }
        return true;
    }

    public Set<BasicBlock> getDefs() {
        return useList.stream()
                .filter(use -> use.getUser() instanceof StoreInst)
                .map(use -> ((StoreInst) use.getUser()).getParent())
                .collect(Collectors.toSet());
    }
}
