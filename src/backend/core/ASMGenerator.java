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

package backend.core;

import backend.mips.Manager;
import backend.mips.Register;
import frontend.llvm.Module;
import frontend.llvm.type.IntegerType;
import frontend.llvm.type.Type;
import frontend.llvm.value.Argument;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.GlobalVariable;
import frontend.llvm.value.user.constant.ConstantInt;
import frontend.llvm.value.user.instr.*;
import frontend.llvm.value.user.instr.pinstr.MoveInst;
import utils.FileUtils;

import java.util.*;

import static backend.core.ASMBuilder.*;
import static backend.mips.Manager.*;
import static backend.mips.Register.*;

public class ASMGenerator {
    private ASMBuilder builder = new ASMBuilder();
    private Map<Value, String> glbMap = new HashMap<>();
    private Map<Value, Register> regMap = new HashMap<>();
    private Map<Value, Integer> memMap = new HashMap<>();
    private Manager manager = new Manager();
    private InstGenerator instGenerator = new InstGenerator();
    private Function curFunction = null;

    public void genFrom(Module module) {
        builder.init();

        /*----------------- build DATA segment -----------------*/
        builder.setSegment(SEG_DATA);
        for (GlobalVariable globalVar : module.getGlobalVariables()) {
            builder.buildGloabalVar(globalVar);
            glbMap.put(globalVar, globalVar.getName());
        }

        /*----------------- build TEXT segment -----------------*/
        builder.setSegment(SEG_TEXT);

        List<Function> functions = new ArrayList<>();
        module.getFunctions().stream()
                .sorted(Comparator.comparing(f -> !"main".equals(f.getName())))
                .forEach(functions::add);

        for (Function function : functions) {
            if (function.isDeclaration()) {
                continue;
            }
            curFunction = function;
            prologue(function);
            function.setInstrName();
            for (BasicBlock bb : function.getBasicBlocks()) {
                builder.buildLabel(getBasicBlockLabel(bb));
                for (Instruction inst : bb.getInstrs()) {
                    builder.buildComment(inst.print());
                    manager.releaseAllReservedTmpReg();
                    inst.accept(instGenerator);
                    builder.buildBreak();
                }
            }
            epilogue(function);
        }
    }

    private void prologue(Function function) {
        // 函数序言
        builder.buildLabel(getFunctionPrologueLabel(function));
        // 预分配栈帧大小
        manager.allocFrame(function);
        // 记录得到寄存器分配的 Value 映射
        regMap.putAll(manager.getRegMap());

        builder.buildAddiu(REG_SP, REG_SP, -manager.getFrameSize());

        if ("main".equals(curFunction.getName())) {
            return;
        }

        builder.buildSw(REG_RA, REG_SP, manager.getRaOffset());
        List<Register> usedRegs = manager.getUsedSavadRegs();
        for (int i = usedRegs.size() - 1; i >= 0; --i) {
            builder.buildSw(usedRegs.get(i), REG_SP, manager.getSaveOffset() + i * WORD_SIZE);
        }
    }

    private void epilogue(Function function) {
        // 函数尾声
        builder.buildLabel(getFunctionEpilogueLabel(function));
        List<Register> usedRegs = manager.getUsedSavadRegs();
        for (int i = 0; i < usedRegs.size(); ++i) {
            builder.buildLw(usedRegs.get(i), REG_SP, manager.getSaveOffset() + i * WORD_SIZE);
        }
        builder.buildLw(REG_RA, REG_SP, manager.getRaOffset());
        builder.buildAddiu(REG_SP, REG_SP, manager.getFrameSize());
        if (function.getName().equals("main")) {
            builder.buildLi(REG_V0, SYS_NO_EXIT);
            builder.buildSyscall();
        } else {
            builder.buildJr(REG_RA);
        }
    }

    private class InstGenerator implements InstVisitor<Void> {
        @Override
        public Void visit(MoveInst inst) {
            // TODO
            Value source = inst.getOperand(0);
            Value target = inst.getOperand(1);
            Register rs = (source instanceof ConstantInt c) ? resolveConstant(c.getValue()) : pull(source);
            Register rd = pull(target);
            builder.buildMove(rd, rs);
            push(rd, target);
            return null;
        }

        @Override
        public Void visit(PhiInst inst) {
            throw new RuntimeException("MIPS backend does not support generation from phi. " +
                    "Did you eliminate the phi(s) before generation?");
        }
        
        @Override
        public Void visit(LoadInst inst) {
            Register rd = pull(inst);
            Register rs = pull(inst.getOperand(0));
            buildLoad(inst.getType().getSize(), rd, rs, 0);
            push(rd, inst);
            return null;
        }

        @Override
        public Void visit(StoreInst inst) {
            Value src = inst.getOperand(0), dest = inst.getOperand(1);
            Register rs = (src instanceof ConstantInt c)
                            ? resolveConstant(c.getValue())
                            : pull(src);
            Register rd = pull(dest);
            buildStore(src.getType().getSize(), rs, rd, 0);
            return null;
        }

        @Override
        public Void visit(BranchInst inst) {
            if (!inst.isCondBr()) {
                builder.buildJ(getBasicBlockLabel((BasicBlock) inst.getOperand(0)));
            } else {
                Register cond = (inst.getOperand(0) instanceof ConstantInt c)
                                    ? resolveConstant(c.getValue())
                                    : pull(inst.getOperand(0));
                builder.buildBne(REG_ZERO, cond, getBasicBlockLabel((BasicBlock) inst.getOperand(1)));
                builder.buildJ(getBasicBlockLabel((BasicBlock) inst.getOperand(2)));
            }
            return null;
        }

        @Override
        public Void visit(AllocaInst inst) {
            Register destReg = pull(inst);
            builder.buildAddiu(destReg, REG_SP, manager.alloc(inst.getAllocaType().getSize()));
            push(destReg, inst);
            return null;
        }

        @Override
        public Void visit(CallInstr inst) {
            Function callee = (Function) inst.getOperand(0);
            for (int i = 1; i < inst.getNumOperand(); ++i) {
                int argno = i - 1;
                Register arg = (inst.getOperand(i) instanceof ConstantInt c)
                                ? resolveConstant(c.getValue())
                                : pull(inst.getOperand(i));
                if (callee.isLeaf() && argno <= 3) {
                    builder.buildMove(ARG_REGS[argno], arg);
                } else {
                    buildStore(inst.getOperand(i).getType().getSize(), arg, REG_SP, argno * WORD_SIZE);
                }

                if (manager.isReservedTmpReg(arg)) {
                    manager.releaseReservedTmpReg(arg);
                }
            }
            if (callee.isDeclaration()) {
                int sysNo = switch (callee.getName()) {
                    case "getint" -> SYS_NO_READ_INT;
                    case "getchar" -> SYS_NO_READ_CHR;
                    case "putint" -> SYS_NO_PRINT_INT;
                    case "putch" -> SYS_NO_PRINT_CHR;
                    case "putstr" -> SYS_NO_PRINT_STR;
                    default -> throw new IllegalStateException("Unexpected lib call: " + callee.getName());
                };
                builder.buildLi(REG_V0, sysNo);
                builder.buildSyscall();
            } else {
                builder.buildJal(getFunctionPrologueLabel(callee));
            }

            if (!callee.getReturnType().isVoidType()) {
                Register rd = pull(inst);
                builder.buildMove(rd, REG_V0);
                push(rd, inst);
            }
            return null;
        }

        @Override
        public Void visit(ReturnInst inst) {
            if ("main".equals(inst.getFunction().getName())) {
                builder.buildLi(REG_V0, SYS_NO_EXIT);
                builder.buildSyscall();
                return null;
            }
            if (!inst.isVoidReturn()) {
                Value op =inst.getOperand(0);
                Register rs = (op instanceof ConstantInt c)
                                ? resolveConstant(c.getValue())
                                : pull(op);
                builder.buildMove(REG_V0, rs);
            }
            builder.buildJ(getFunctionEpilogueLabel(inst.getFunction()));
            return null;
        }

        @Override
        public Void visit(GetElementPtrInst inst) {
            Value ptr = inst.getOperand(0);
            Type ty = ptr.getType().getPointerElementType();

            List<Integer> offsetSizes = new ArrayList<>();
            List<Value> offsets = new ArrayList<>();

            for (int i = 1; i < inst.getNumOperand(); ++i) {
                Value offset = inst.getOperand(i);
                if (!(offset instanceof ConstantInt c && c.getValue() == 0)) {
                    offsetSizes.add(ty.getSize());
                    offsets.add(offset);
                }
                if (i < inst.getNumOperand() - 1) {
                    ty = ty.getArrayElementType();
                }
            }

            Register base = pull(ptr), rd = pull(inst);
            if (offsetSizes.isEmpty()) {
                builder.buildMove(rd, base);
                push(rd, inst);
                return null;
            }

            for (int i = 0; i < offsetSizes.size(); ++i) {
                Register tmpReg;
                if (offsets.get(i) instanceof ConstantInt c) {
                    tmpReg = resolveConstant(c.getValue() * offsetSizes.get(i));
                } else {
                    switch (offsetSizes.get(i)) {
                        case BYTE_SIZE -> tmpReg = pull(offsets.get(i));
                        case WORD_SIZE -> {
                            tmpReg = manager.getReservedTmpReg();
                            builder.buildSll(tmpReg, pull(offsets.get(i)), 2);
                        }
                        default -> throw new RuntimeException("Unexpected offset size: " + offsetSizes.get(i));
                    }
                }
                if (i == 0) {
                    builder.buildAddu(rd, base, tmpReg);
                    if (manager.isReservedTmpReg(base)) {
                        manager.releaseReservedTmpReg(base);
                    }
                } else {
                    builder.buildAddu(rd, rd, tmpReg);
                }
                if (manager.isReservedTmpReg(tmpReg)) {
                    manager.releaseReservedTmpReg(tmpReg);
                }
            }
            push(rd, inst);
            return null;
        }

        @Override
        public Void visit(CastInst inst) {
            Value op = inst.getOperand(0);
            //if (op instanceof ConstantInt) {
            //    throw new RuntimeException("did you solve the compile-time constant when generating ir?");
            //}
            Register rs = (op instanceof ConstantInt c) ? resolveConstant(c.getValue()) : pull(op);
            Register rd = pull(inst);
            switch (inst.getInstrType()) {
                case ZEXT -> builder.buildMove(rd, rs);
                case TRUNCT -> {
                    int mask = (1 << ((IntegerType) inst.getDestType()).getNumBits()) - 1;
                    builder.buildAndi(rd, rs, mask);
                }
                default -> throw new RuntimeException("unknown instruction type: " + inst.getInstrType());
            }
            push(rd, inst);
            return null;
        }

        @Override
        public Void visit(ICmpInst inst) {
            Value op1 = inst.getOperand(0), op2 = inst.getOperand(1);
            //if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            //    throw new RuntimeException("error @ " + inst.print() + ". did you solve the compile-time constant when generating ir?");
            //}
            Register rd = pull(inst);
            Register rs1 = (op1 instanceof ConstantInt c1) ? resolveConstant(c1.getValue()) : pull(op1);
            Register rs2 = (op2 instanceof ConstantInt c2) ? resolveConstant(c2.getValue()) : pull(op2);
            switch (inst.getInstrType()) {
                case LT -> builder.buildSlt(rd, rs1, rs2);
                case LE -> builder.buildSle(rd, rs1, rs2);
                case GT -> builder.buildSgt(rd, rs1, rs2);
                case GE -> builder.buildSge(rd, rs1, rs2);
                case EQ -> builder.buildSeq(rd, rs1, rs2);
                case NE -> builder.buildSne(rd, rs1, rs2);
                default -> throw new RuntimeException("unknown instr type");
            }
            push(rd, inst);
            return null;
        }

        @Override
        public Void visit(IBinaryInst inst) {
            Value op1 = inst.getOperand(0), op2 = inst.getOperand(1);
            //if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            //    throw new RuntimeException("did you solve the compile-time constant when generating ir?");
            //}
            //if (checkAddPerf(inst) || checkMultPerf(inst)) {
            //    return null;
            //}
            Register rd = pull(inst);
            Register rs1 = (op1 instanceof ConstantInt c1) ? resolveConstant(c1.getValue()) : pull(op1);
            Register rs2 = (op2 instanceof ConstantInt c2) ? resolveConstant(c2.getValue()) : pull(op2);
            switch (inst.getInstrType()) {
                case ADD -> builder.buildAddu(rd, rs1, rs2);
                case SUB -> builder.buildSubu(rd, rs1, rs2);
                case MUL -> builder.buildMul(rd, rs1, rs2);
                case SDIV -> builder.buildDiv(rd, rs1, rs2);
                case SREM -> builder.buildRem(rd, rs1, rs2);
                default -> throw new RuntimeException("Unsupported inst type: " + inst.getInstrType());
            }
            push(rd, inst);
            return null;
        }
    }

    private Register resolveConstant(int num) {
        if (num == 0) {
            return REG_ZERO;
        }
        Register tmpReg = manager.getReservedTmpReg();
        if (!canImmediateHold(num)) {
            builder.buildLui(tmpReg, getHigh16Bits(num));
            builder.buildOri(tmpReg, tmpReg, getLow16Bits(num));
        } else {
            builder.buildOri(tmpReg, REG_ZERO, getLow16Bits(num));
        }
        return tmpReg;
    }

    private Register pull(Value value) {
        if (regMap.containsKey(value)) {
            return regMap.get(value);
        }

        Register tmpReg = manager.getReservedTmpReg();
        if (value instanceof Argument arg) {
            int argno = arg.getArgno();
            if (curFunction.isLeaf() && argno <= 3) {
                // 从参数寄存器取得实参
                //builder.buildMove(tmpReg, ARG_REGS[argno]);
                return ARG_REGS[argno];
            } else {
                // 从调用者栈帧的栈顶取得实参
                buildLoad(value.getType().getSize(), tmpReg, REG_SP, manager.getFrameSize() + argno * WORD_SIZE);
            }
        } else if (glbMap.containsKey(value)) {
            builder.buildLa(tmpReg, glbMap.get(value));
        } else if (memMap.containsKey(value)){
            buildLoad(value.getType().getSize(), tmpReg, REG_SP, memMap.get(value));
        } else {
            memMap.put(value, manager.alloc(WORD_SIZE));
        }
        return tmpReg;
    }

    private void push(Register reg, Value value) {
        if (regMap.containsKey(value)) {
            return;
        }

        if (value instanceof Argument) {
            throw new RuntimeException("Argument on the caller's stack frame cannot be modified");
        } else if (glbMap.containsKey(value)) {
            throw new RuntimeException("Global Variable on the data segment cannot be modified");
        } else if (memMap.containsKey(value)) {
            buildStore(value.getType().getSize(), reg, REG_SP, memMap.get(value));
        } else {
            throw new RuntimeException("No memory or register are allocated for the value: " + value.print());
        }
    }

    private void buildLoad(int nBytes, Register dest, Register base, int offset) {
        if (nBytes == 1) {
            builder.buildLb(dest, base, offset);
        } else if (nBytes == 4 || nBytes == 8) {
            builder.buildLw(dest, base, offset);
        } else {
            throw new RuntimeException("NBytes must be 1, 4 or 8 but got " + nBytes);
        }
    }

    private void buildStore(int nBytes, Register src, Register base, int offset) {
        if (nBytes == 1) {
            builder.buildSb(src, base, offset);
        } else if (nBytes == 4 || nBytes == 8) {
            builder.buildSw(src, base, offset);
        } else {
            throw new RuntimeException("NBytes must be 1, 4 or 8 but got " + nBytes);
        }
    }

    /******************************** api about immediate ********************************/
    public static int getHigh16Bits(int num) {return (num >>> 16) & 0xFFFF;}
    public static int getLow16Bits(int num) {return num & 0xFFFF;}
    public static boolean canImmediateHold(int num) {return (num >>> 16) == 0;}

    /******************************** api about mul/div opt ********************************/
    public static boolean isPowerOfTwo(int num) {
        if (num <= 0) {
            return false;
        }
        return (num & (num - 1)) == 0;
    }

    public static int log2(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("Input must be a positive integer");
        }
        int count = 0;
        while (num > 1) {
            num >>= 1;
            count++;
        }
        return count;
    }

    /******************************** api about truncation ********************************/
    public static boolean requireTruncation(int val, int numBits) {
        int mask = (1 << numBits) - 1;
        int truncatedValue = val & mask;
        return val != truncatedValue;
    }

    public void dump(String filePath) {
        FileUtils.writeStringToFile(filePath, builder.dump());
    }

    private String getFunctionEpilogueLabel(Function function) {
        return "epilogue_" + function.getName();
    }

    private String getFunctionPrologueLabel(Function function) {
        return "prologue_" + function.getName();
    }

    private String getBasicBlockLabel(BasicBlock bb) {
        return bb.getName() + "_at_" + bb.getParent().getName();
    }

    private boolean checkAddPerf(IBinaryInst inst) {
        if (!inst.isAdd()) {
            return false;
        }
        Value op1 = inst.getOperand(0), op2 = inst.getOperand(1);
        if (op1 instanceof ConstantInt c1 && canImmediateHold(c1.getValue())) {
            Register rd = pull(inst);
            builder.buildAddiu(rd, pull(op2), c1.getValue());
            push(rd, inst);
            return true;
        } else if (op2 instanceof ConstantInt c2 && canImmediateHold(c2.getValue())) {
            Register rd = pull(inst);
            builder.buildAddiu(rd, pull(op1), c2.getValue());
            push(rd, inst);
            return true;
        }
        return false;
    }

    private boolean checkMultPerf(IBinaryInst inst) {
        if (!inst.isMul()) {
            return false;
        }
        Value op1 = inst.getOperand(0), op2 = inst.getOperand(1);
        if (op1 instanceof ConstantInt c1 && isPowerOfTwo(c1.getValue())) {
            Register rd = pull(inst);
            builder.buildSll(rd, pull(op2), log2(c1.getValue()));
            push(rd, inst);
            return true;
        } else if (op2 instanceof ConstantInt c2 && isPowerOfTwo(c2.getValue())) {
            Register rd = pull(inst);
            builder.buildSll(rd, pull(op1), log2(c2.getValue()));
            push(rd, inst);
            return true;
        }
        return false;
    }

    private boolean checkDivPerf(IBinaryInst inst) {
        if (!inst.isDiv()) {
            return false;
        }
       if (inst.getOperand(1) instanceof ConstantInt c2 && isPowerOfTwo(c2.getValue())) {
            Register rd = pull(inst);
            builder.buildSra(rd, pull(inst.getOperand(0)), log2(c2.getValue()));
            push(rd, inst);
            return true;
        }
        return false;
    }

}

