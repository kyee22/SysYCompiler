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
import frontend.llvm.type.FunctionType;
import frontend.llvm.type.Type;
import frontend.llvm.value.Argument;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;

import java.util.List;

public class CallInstr extends Instruction {
    private CallInstr(Function function, List<Value> arguments, BasicBlock parent) {
        super(function.getReturnType(), OpID.CALL, parent);
        if (!function.getType().isFunctionType()) {
            throw new IllegalArgumentException("function type expected");
        }
        if (function.getNumArgs() != arguments.size()) {
            throw new IllegalArgumentException("arguments size mismatch");
        }
        addOperand(function);
        FunctionType functionType = (FunctionType) function.getType();
        for (int i = 0; i < arguments.size(); i++) {
            if (functionType.getParamType(i) != arguments.get(i).getType()) {
                throw new IllegalArgumentException("arguments mismatch");
            }
            addOperand(arguments.get(i));
        }
    }

    public static CallInstr createCall(Function function, List<Value> arguments, BasicBlock parent) {
        return new CallInstr(function, arguments, parent);
    }

    public FunctionType getFunctionType() {
        return (FunctionType) getOperand(0).getType();
    }

    @Override
    public String print() {
        StringBuilder instIr = new StringBuilder();
        if (!isVoid()) {
            instIr.append("%");
            instIr.append(getName());
            instIr.append(" = ");
        }

        instIr.append(getInstrOpName());
        instIr.append(" ");
        instIr.append(getFunctionType().getReturnType().print());

        instIr.append(" ");
        instIr.append(IRPrinter.printAsOp(getOperand(0), false));
        instIr.append("(");
        for (int i = 1; i < getNumOperand(); ++i) {
            if (i > 1) {
                instIr.append(", ");
            }
            instIr.append(getOperand(i).getType().print());
            instIr.append(" ");
            instIr.append(IRPrinter.printAsOp(getOperand(i), false));
        }
        instIr.append(")");
        return instIr.toString();
    }
}
