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
import utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // todo 立即数过大?

    public void genFrom(Module module) {
        builder.init();

        builder.setSegment(SEG_DATA);
        for (GlobalVariable globalVar : module.getGlobalVariables()) {
            builder.buildGloabalVar(globalVar);
            glbMap.put(globalVar, globalVar.getName());
        }

        builder.setSegment(SEG_TEXT);

        List<Function> functions = new ArrayList<>();
        for (Function function : module.getFunctions()) {
            if ("main".equals(function.getName())) {
                functions.add(function);
                break;
            }
        }
        for (Function function : module.getFunctions()) {
            if (!"main".equals(function.getName())) {
                functions.add(function);
            }
        }

        for (Function function : functions) {
            if (function.isDeclaration()) {
                continue;
            }
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

        builder.buildSubiu(REG_SP, REG_SP, manager.getFrameSize());
        builder.buildSw(REG_RA, REG_SP, manager.getRaOffset());
        //todo: 后面记得更改
        //for (int i = SAVED_REGS.length - 1; i >= 0; --i) {
        //    builder.buildSw(SAVED_REGS[i], REG_SP, manager.getSaveOffset() + i * 4);
        //}
    }

    private void epilogue(Function function) {
        // 函数尾声
        builder.buildLabel(getFunctionEpilogueLabel(function));

        //for (int i = 0; i < SAVED_REGS.length; ++i) {
        //    builder.buildLw(SAVED_REGS[i], REG_SP, manager.getSaveOffset() + i * WORD_SIZE);
        //}
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
            // todo: 库函数
            for (int i = 1; i < inst.getNumOperand(); ++i) {
                int argno = i - 1;
                Register arg = (inst.getOperand(i) instanceof ConstantInt c)
                                ? resolveConstant(c.getValue())
                                : pull(inst.getOperand(i));
                if (argno <= 3) {
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
            if (offsetSizes.isEmpty()) {
                copyLocation(inst, ptr);
            } else {
                Register base = pull(ptr);
                Register rd = pull(inst), rs, tmpReg = manager.getReservedTmpReg();
                for (int i = 0; i < offsetSizes.size(); ++i) {
                    int offsetSize = offsetSizes.get(i);
                    Value offset = offsets.get(i);
                    // todo: 如果很多维数组的话可能用很多寄存器
                    rs = (offset instanceof ConstantInt c)
                            ? resolveConstant(c.getValue())
                            : pull(offset);
                    if (offsetSize == 1) {
                        tmpReg = rs;
                    } else if (offsetSize == 4) {
                        builder.buildSll(tmpReg, rs, 2);
                    } else {
                        throw new RuntimeException("Unexpected offset size: " + offsetSize);
                    }
                    if (manager.isReservedTmpReg(rs)) {
                        manager.releaseReservedTmpReg(rs);
                    }
                    if (i == 0) {
                        builder.buildAddu(rd, base, tmpReg);
                        if (manager.isReservedTmpReg(base)) {
                            manager.releaseReservedTmpReg(base);
                        }
                    } else {
                        builder.buildAddu(rd, rd, tmpReg);
                    }
                }
                push(rd, inst);
            }
            return null;
        }

        @Override
        public Void visit(CastInst inst) {
            Value op = inst.getOperand(0);
            if (op instanceof ConstantInt) {
                throw new RuntimeException("did you solve the compile-time constant when generating ir?");
            }
            Register rs = pull(op);
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
            if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
                throw new RuntimeException("error @ " + inst.print() + ". did you solve the compile-time constant when generating ir?");
            }
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
            if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
                throw new RuntimeException("did you solve the compile-time constant when generating ir?");
            }
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

            //switch (inst.getInstrType()) {
            //    case ADD -> genAdd(rd, op1, op2);
            //    case SUB -> genSub(rd, op1, op2);
            //    case MUL -> genMul(rd, op1, op2);
            //    case SDIV -> genDiv(rd, op1, op2);
            //    case SREM -> genRem(rd, op1, op2);
            //    default -> throw new RuntimeException("Unsupported inst type: " + inst.getInstrType());
            //}
            push(rd, inst);
            return null;
        }

        private void genAdd(Register rd, Value op1, Value op2) {
            Register rs1, rs2;
            if (op1 instanceof ConstantInt || op2 instanceof ConstantInt) {
                Value op1_ = op1, op2_ = op2;
                op1 = (op1_ instanceof ConstantInt) ? op2_ : op1_;
                op2 = (op1_ instanceof ConstantInt) ? op1_ : op2_;
                ConstantInt c2 = (ConstantInt) op2;
                rs1 = pull(op1);
                if (canImmediateHold(c2.getValue())) {
                    builder.buildAddiu(rd, rs1, c2.getValue());
                } else {
                    builder.buildAddu(rd, rs1, resolveConstant(c2.getValue()));
                }
            } else {
                rs1 = pull(op1);
                rs2 = pull(op2);
                builder.buildAddu(rd, rs1, rs2);
            }
        }

        // todo: 减法不具备交换律
        private void genSub(Register rd, Value op1, Value op2) {
            Register rs1, rs2;
            if (op1 instanceof ConstantInt c1) {
                rs1 = resolveConstant(c1.getValue());
                rs2 = pull(op2);
                builder.buildSubu(rd, rs1, rs2);
            } else if (op2 instanceof ConstantInt c2) {
                rs1 = pull(op1);
                if (canImmediateHold(c2.getValue())) {
                    builder.buildSubiu(rd, rs1, c2.getValue());
                } else {
                    builder.buildSubu(rd, rs1, resolveConstant(c2.getValue()));
                }
            } else {
                rs1 = pull(op1);
                rs2 = pull(op2);
                builder.buildSubu(rd, rs1, rs2);
            }
        }

        private void genMul(Register rd, Value op1, Value op2) {
            Register rs1 = (op1 instanceof ConstantInt c1) ? resolveConstant(c1.getValue()) : pull(op1);
            Register rs2 = (op2 instanceof ConstantInt c2) ? resolveConstant(c2.getValue()) : pull(op2);
            builder.buildMul(rd, rs1, rs2);
        }

        private void genDiv(Register rd, Value op1, Value op2) {
            Register rs1 = (op1 instanceof ConstantInt c1) ? resolveConstant(c1.getValue()) : pull(op1);
            Register rs2 = (op2 instanceof ConstantInt c2) ? resolveConstant(c2.getValue()) : pull(op2);
            builder.buildDiv(rd, rs1, rs2);
        }

        public void genRem(Register rd, Value op1, Value op2) {
            Register rs1 = (op1 instanceof ConstantInt c1) ? resolveConstant(c1.getValue()) : pull(op1);
            Register rs2 = (op2 instanceof ConstantInt c2) ? resolveConstant(c2.getValue()) : pull(op2);
            builder.buildRem(rd, rs1, rs2);
        }


    }

    private Register resolveConstant(int num) {
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
            if (argno <= 3) {
                // 从参数寄存器取得实参
                builder.buildMove(tmpReg, ARG_REGS[argno]);
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

    private void copyLocation(Value dest, Value src) {
        if (regMap.containsKey(src)) {
            regMap.put(dest, regMap.get(src));
        } else if (memMap.containsKey(src)) {
            memMap.put(dest, memMap.get(src));
        } else if (glbMap.containsKey(src)) {
            glbMap.put(dest, glbMap.get(src));
        } else {
            throw new RuntimeException("No memory or register are allocated for the value: " + src.print());
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

    /******************************** api about truncation ********************************/
    public static boolean needsTruncation(int val, int numBits) {
        int mask = (1 << numBits) - 1;
        int truncatedValue = val & mask;
        return val != truncatedValue;
    }

    public static int truncateLowBits(int value, int numBits) {
        if (numBits < 0 || numBits > 31) {
            throw new IllegalArgumentException("numBits must be between 0 and 31");
        }
        int mask = ((1 << numBits) - 1);
        return value & mask;
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

}

