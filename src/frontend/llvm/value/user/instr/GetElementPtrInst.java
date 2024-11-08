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

import frontend.llvm.IRPrinter;
import frontend.llvm.type.ArrayType;
import frontend.llvm.type.PointerType;
import frontend.llvm.type.Type;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Value;

import java.util.List;

public class GetElementPtrInst extends Instruction {
    private GetElementPtrInst(Value ptr, List<Value> idxs, BasicBlock bb) {
        super(PointerType.get(getElementType(ptr, idxs)), OpID.GETELEMENTPTR, bb);
        addOperand(ptr);
        for (Value idx : idxs) {
            if (!(idx.getType().isIntegerType())) {
                throw new IllegalArgumentException("Index is not integer");
            }
            addOperand(idx);
        }
    }

    public static GetElementPtrInst createGep(Value ptr, List<Value> idxs, BasicBlock bb) {
        return new GetElementPtrInst(ptr, idxs, bb);
    }


    private static Type getElementType(Value ptr, List<Value> idxs) {
        if (!ptr.getType().isPointerType()) {
            throw new IllegalArgumentException("GetElementPtrInst ptr is not a pointer");
        }

        Type ty = ptr.getType().getPointerElementType();
        if (!(ty.isArrayType() || ty.isIntegerType() || ty.isFloatType())) {
            throw new IllegalArgumentException("GetElementPtrInst ptr is wrong type: " + ty.print());
        }

        if (ty.isArrayType()) {
            ArrayType arrTy = (ArrayType) ty;
            for (int i = 1; i < idxs.size(); i++) {
                ty = arrTy.getElementType();
                if (i < idxs.size() - 1) {
                    if (!ty.isArrayType()) {
                        throw new IllegalArgumentException("Index error!");
                    }
                }
                if (ty.isArrayType()) {
                    arrTy = (ArrayType) ty;
                }
            }
        }
        return ty;
    }

    public Type getElementType() {
        return getType().getPointerElementType();
    }

    @Override
    public String print() {
        StringBuilder instrIr = new StringBuilder();
        instrIr.append("%").append(getName()).append(" = ").append(getInstrOpName()).append(" ");
        if (!(getOperand(0).getType().isPointerType())) {
            throw new IllegalArgumentException("First operand must be a pointer type");
        }
        instrIr.append(getOperand(0).getType().getPointerElementType().print()).append(", ");
        for (int i = 0; i < getNumOperand(); i++) {
            if (i > 0) {
                instrIr.append(", ");
            }
            instrIr.append(getOperand(i).getType().print())
                    .append(" ")
                    .append(IRPrinter.printAsOp(getOperand(i), false));
        }
        return instrIr.toString();
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
