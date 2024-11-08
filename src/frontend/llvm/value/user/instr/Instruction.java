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
import frontend.llvm.type.Type;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.user.User;
import frontend.llvm.Module;

public abstract class Instruction extends User {
    private OpID opId;
    private BasicBlock parent;

    public enum OpID {
            // Terminator Instructions
            RET, BR,
            // Standard binary operators
            ADD, SUB, MUL, SDIV, SREM,
            // float binary operators
            FADD, FSUB, FMUL, FDIV,
            // Memory operators
            ALLOCA, LOAD, STORE,
            // Int compare operators
            GE, GT, LE, LT, EQ, NE,
            // Float compare operators
            FGE, FGT, FLE, FLT, FEQ, FNE,
            // Other operators
            PHI, CALL, GETELEMENTPTR, ZEXT, SEXT, TRUNCT,// zero extend
            FPTOSI, SITOFP
    }

    public Instruction(Type type, OpID id, BasicBlock parent) {
        super(type, "");
        this.opId = id;
        this.parent = parent;
        if (parent != null) {
            parent.addInstr(this);
        }
    }

    public BasicBlock getParent() {
        return parent;
    }

    public void setParent(BasicBlock parent) {
        this.parent = parent;
    }

    public Function getFunction() {
        return parent.getParent();
    }

    public Module getModule() {
        return parent.getModule();
    }

    public OpID getInstrType() {return opId;}
    public String getInstrOpName() {return IRPrinter.printInstrOpName(opId);}

    public boolean isVoid() {
        return (opId == OpID.RET || opId == OpID.BR || opId == OpID.STORE
                || (opId == OpID.CALL && this.getType().isVoidType()));
    }

    public boolean isPhi() { return opId == OpID.PHI; }
    public boolean isStore() { return opId == OpID.STORE; }
    public boolean isAlloca() { return opId == OpID.ALLOCA; }
    public boolean isRet() { return opId == OpID.RET; }
    public boolean isLoad() { return opId == OpID.LOAD; }
    public boolean isBr() { return opId == OpID.BR; }
    public boolean isAdd() { return opId == OpID.ADD; }
    public boolean isSub() { return opId == OpID.SUB; }
    public boolean isMul() { return opId == OpID.MUL; }
    public boolean isDiv() { return opId == OpID.SDIV; }
    public boolean isRem() { return opId == OpID.SREM; }
    public boolean isFAdd() { return opId == OpID.FADD; }
    public boolean isFSub() { return opId == OpID.FSUB; }
    public boolean isFMul() { return opId == OpID.FMUL; }
    public boolean isFDiv() { return opId == OpID.FDIV; }
    public boolean isFP2SI() { return opId == OpID.FPTOSI; }
    public boolean isSI2FP() { return opId == OpID.SITOFP; }
    public boolean isCmp() { return OpID.GE.ordinal() <= opId.ordinal() && opId.ordinal() <= OpID.NE.ordinal(); }
    public boolean isFCmp() { return OpID.FGE.ordinal() <= opId.ordinal() && opId.ordinal() <= OpID.FNE.ordinal(); }
    public boolean isCall() { return opId == OpID.CALL; }
    public boolean isGEP() { return opId == OpID.GETELEMENTPTR; }
    public boolean isZExt() { return opId == OpID.ZEXT; }
    public boolean isSExt() {return opId == OpID.SEXT; }

    public boolean isBinary() {
        return (isAdd() || isSub() || isMul() || isDiv() || isRem()
                || isFAdd() || isFSub() || isFMul() || isFDiv()) && (getNumOperand() == 2);
    }

    public boolean isTerminator() {
        return isBr() || isRet();
    }

    public <T> T accept(InstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

