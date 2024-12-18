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

import frontend.llvm.type.ArrayType;
import frontend.llvm.type.Type;
import frontend.llvm.value.user.GlobalVariable;
import frontend.llvm.value.user.constant.Constant;
import frontend.llvm.value.user.constant.ConstantArray;
import frontend.llvm.value.user.constant.ConstantInt;
import frontend.llvm.value.user.constant.ConstantZero;

public class ASMPrinter {

    public static String print(GlobalVariable globalVar) {
        StringBuilder sb = new StringBuilder();
        sb.append(globalVar.getName())
          .append(": ")
          .append(print(globalVar.getType().getPointerElementType()))
          .append(" ")
          .append(print(globalVar.getInit()));

        return sb.toString();
    }

    public static String print(Type type) {
        if (type.isInt32Type()) {
            return ".word";
        } else if (type.isInt8Type()) {
            return ".byte";
        } else if (type.isArrayType()) {
            return print(type.getArrayElementType());
        }
        throw new RuntimeException("Unsupported type: " + type.print());
    }

    public static String print(Constant constant) {
        if (constant instanceof ConstantZero constantZero) {
            return (constantZero.getType() instanceof ArrayType arrTy)
                    ? "0:" + arrTy.getNumberOfElements()
                    : "0";
        } else if (constant instanceof ConstantInt constantInt) {
            return String.valueOf(constantInt.getValue());
        } else if (constant instanceof ConstantArray constantArray) {
            StringBuilder sb = new StringBuilder();
            sb.append(print(constantArray.getElementValue(0)));
            for (int i = 1; i < constantArray.getSizeOfArray(); ++i) {
                sb.append(", ");
                sb.append(print(constantArray.getElementValue(i)));
            }
            return sb.toString();
        }
        throw new RuntimeException("Unsupported constant: " + constant.print() + " (Java class: " + constant.getClass().getSimpleName() + ")");
    }

    public static String printOp0(Object op) {
        return "\t" + op.toString() + "\n";
    }

    public static String printOp1(Object op, Object op1) {
        return String.format("\t%-8s%s\n", op.toString(), op1.toString());
    }

    public static String printOp2(Object op, Object op1, Object op2) {
        return String.format("\t%-8s%s, %s\n", op.toString(), op1.toString(), op2.toString());
    }

    public static String printOp3(Object op, Object op1, Object op2, Object op3) {
        return String.format("\t%-8s%s, %s, %s\n", op.toString(), op1.toString(), op2.toString(), op3.toString());
    }

    public static String printLoad(Object op, Object dest, Object base, Object offset) {
        return String.format("\t%-8s%s, %s(%s)\n", op.toString(), dest.toString(), offset.toString(), base.toString());
    }

    public static String printStore(Object op, Object src, Object base, Object offset) {
        return String.format("\t%-8s%s, %s(%s)\n", op.toString(), src.toString(), offset.toString(), base.toString());
    }
}
