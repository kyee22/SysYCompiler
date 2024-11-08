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

import backend.regalloc.BaseRegAllocator;
import backend.regalloc.RegAllocator;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.AllocaInst;
import frontend.llvm.value.user.instr.CallInstr;
import frontend.llvm.value.user.instr.Instruction;

import java.sql.Ref;
import java.util.*;

import static backend.mips.Register.*;

public class Manager {
    private int frameSize;
    private int allocTop;
    private int saveOffset;
    private int raOffset;
    private int localOffset;
    private RegAllocator regAllocator = new BaseRegAllocator();
    private Map<Value, Register> regMap;

    public static final int WORD_SIZE = 4;

    public static final Register[]SAVED_REGS = new Register[]{REG_S0, REG_S1, REG_S2, REG_S3, REG_S4, REG_S5, REG_S6, REG_S7};
    public static final Register[]ARG_REGS = new Register[]{REG_A0, REG_A1, REG_A2, REG_A3};

    public static final int SYS_NO_PRINT_INT = 1;
    public static final int SYS_NO_PRINT_STR = 4;
    public static final int SYS_NO_PRINT_CHR = 11;
    public static final int SYS_NO_READ_INT = 5;
    public static final int SYS_NO_READ_CHR = 12;
    public static final int SYS_NO_EXIT = 10;

    private final LinkedHashSet<Register> reservedTmpRegs = new LinkedHashSet<>(Arrays.asList(REG_T3, REG_T2, REG_T1, REG_T0));
    private Stack<Register> reservedTmpRegsPool = new Stack<>();


    public void releaseAllReservedTmpReg() {
        reservedTmpRegsPool.clear();
        reservedTmpRegsPool.addAll(reservedTmpRegs);
    }

    public void releaseReservedTmpReg(Register reg) {
        if (!isReservedTmpReg(reg)) {
            throw new RuntimeException("Reserved tmp register is not a reserved tmp register");
        }
        if (reservedTmpRegsPool.contains(reg)) {
            throw new RuntimeException("Reserved tmp register is already in the pool");
        }
        reservedTmpRegsPool.push(reg);
    }

    public boolean isReservedTmpReg(Register reg) {
        return reservedTmpRegs.contains(reg);
    }

    public Register getReservedTmpReg() {
        return reservedTmpRegsPool.pop();
    }

    public void allocFrame(Function function) {
        List<Value> vRegs = new ArrayList<>();
        int allocaSize = 0;
        int argsSize = 0;

        regMap = regAllocator.allocate(function);

        for (BasicBlock bb : function.getBasicBlocks()) {
            for (Instruction inst : bb.getInstrs()) {
                if (!inst.isVoid() || !regMap.containsKey(inst)) {
                    vRegs.add(inst);
                }
                if (inst instanceof AllocaInst allocaInst) {
                    allocaSize += roundUpTo4(allocaInst.getAllocaType().getSize());
                }
                if (inst instanceof CallInstr callInst) {
                    argsSize = Math.max(argsSize, callInst.getFunctionType().getNumberOfArgs() * WORD_SIZE);
                }
            }
        }

        saveOffset = argsSize;
        raOffset = saveOffset + 8 * WORD_SIZE; // 先将所有 $s 保存
        localOffset = raOffset + 8;

        frameSize = localOffset + vRegs.size() * WORD_SIZE + allocaSize;
        allocTop = frameSize;
    }

    public int alloc(int size) {
        size = roundUpTo4(size);
        allocTop -= size;
        return allocTop;
    }

    public int getSaveOffset() {return saveOffset;}
    public int getRaOffset() {return raOffset;}
    public int getFrameSize() {return frameSize;}
    public Map<Value, Register> getRegMap() {return regMap;}

    private int roundUpTo4(int num) {return (num + 3) & ~3;}
}
