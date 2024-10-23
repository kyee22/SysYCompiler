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

package frontend.llvm;

import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.GlobalVariable;
import frontend.llvm.value.user.constant.Constant;
import frontend.llvm.value.user.instr.Instruction;
import frontend.llvm.value.user.instr.Instruction.OpID;

public class IRPrinter {
    public static String printAsOp(Value value, boolean printType) {
        StringBuilder sb = new StringBuilder();
        if (printType) {
            sb.append(value.getType().print());
            sb.append(" ");
        }

        if (value instanceof GlobalVariable) {
            sb.append("@" + value.getName());
        } else if (value instanceof Function) {
            sb.append("@" + value.getName());
        } else if (value instanceof Constant) {
            sb.append(((Constant) value).print());
        } else {
            sb.append("%" + value.getName());
        }

        return sb.toString();
    }

    public static String printInstrOpName(OpID id) {
        switch (id) {
            case RET:
                return "ret";
            case BR:
                return "br";
            case ADD:
                return "add";
            case SUB:
                return "sub";
            case MUL:
                return "mul";
            case SDIV:
                return "sdiv";
            case FADD:
                return "fadd";
            case FSUB:
                return "fsub";
            case FMUL:
                return "fmul";
            case FDIV:
                return "fdiv";
            case ALLOCA:
                return "alloca";
            case LOAD:
                return "load";
            case STORE:
                return "store";
            case GE:
                return "sge";
            case GT:
                return "sgt";
            case LE:
                return "sle";
            case LT:
                return "slt";
            case EQ:
                return "eq";
            case NE:
                return "ne";
            case FGE:
                return "uge";
            case FGT:
                return "ugt";
            case FLE:
                return "ule";
            case FLT:
                return "ult";
            case FEQ:
                return "ueq";
            case FNE:
                return "une";
            case PHI:
                return "phi";
            case CALL:
                return "call";
            case GETELEMENTPTR:
                return "getelementptr";
            case ZEXT:
                return "zext";
            case SEXT:
                return "sext";
            case FPTOSI:
                return "fptosi";
            case SITOFP:
                return "sitofp";
            default:
                throw new IllegalArgumentException("Must be bug");
        }
    }

    public static String printBinaryInst(Instruction inst) {
        StringBuilder instrIr = new StringBuilder();
        instrIr.append("%");
        instrIr.append(inst.getName());
        instrIr.append(" = ");
        instrIr.append(inst.getInstrOpName());
        instrIr.append(" ");
        instrIr.append(inst.getOperand(0).getType().print());
        instrIr.append(" ");
        instrIr.append(printAsOp(inst.getOperand(0), false));
        instrIr.append(", ");

        if (inst.getOperand(0).getType().equals(inst.getOperand(1).getType())) {
            instrIr.append(printAsOp(inst.getOperand(1), false));
        } else {
            instrIr.append(printAsOp(inst.getOperand(1), true));
        }

        return instrIr.toString();
    }

    public static String printCmpInts(Instruction inst) {
        StringBuilder instrIr = new StringBuilder();
        String cmpType;
        if (inst.isCmp()) {
            cmpType = "icmp";
        } else if (inst.isFCmp()) {
            cmpType = "fcmp";
        } else {
            throw new IllegalArgumentException("Unexpected case");
        }

        instrIr.append("%");
        instrIr.append(inst.getName());
        instrIr.append(" = " + cmpType + " ");
        instrIr.append(inst.getInstrOpName());
        instrIr.append(" ");
        instrIr.append(inst.getOperand(0).getType().print());
        instrIr.append(" ");
        instrIr.append(printAsOp(inst.getOperand(0), false));
        instrIr.append(", ");
        if (inst.getOperand(0).getType().equals(inst.getOperand(1).getType())) {
            instrIr.append(printAsOp(inst.getOperand(1), false));
        } else {
            instrIr.append(printAsOp(inst.getOperand(1), true));
        }
        return instrIr.toString();
    }
}
