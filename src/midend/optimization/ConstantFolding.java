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

package midend.optimization;

import frontend.llvm.Module;
import frontend.llvm.type.Type;
import frontend.llvm.value.Function;
import frontend.llvm.value.Use;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.constant.ConstantInt;
import frontend.llvm.value.user.instr.CastInst;
import frontend.llvm.value.user.instr.IBinaryInst;
import frontend.llvm.value.user.instr.ICmpInst;
import frontend.llvm.value.user.instr.Instruction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static utils.StringUtils.sha1;

public class ConstantFolding implements Pass {
    private Map<String, Value> map;
    private Queue<Value> worklist;

    @Override
    public void run(Function function) {
        map = new HashMap<>();
        worklist = new LinkedList<>();
        worklist.addAll(function.getInstructions());
        while (!worklist.isEmpty()) {
            fold(worklist.poll());
        }
    }

    private void fold(Value inst) {
        if (inst instanceof ICmpInst || inst instanceof IBinaryInst) {
            foldBinary((Instruction) inst);
        }
        if (inst instanceof CastInst castInst) {
            foldCast(castInst);
        }
    }

    private void foldCast(CastInst castInst) {
        // 重要!! 如果一个 inst 以及 eraseFromParent, 那它的 operand 和 parent 信息都被清掉了,
        // 但它可能还在 worklist 里, 如果不做这个判断, 往下进行会导致空指针异常!
        if (castInst.getParent() == null) {
            return;
        }
        Value value = castInst.getOperand(0);
        Type destType = castInst.getDestType();
        if (value instanceof ConstantInt constValue) {
            int val = constValue.getValue();
            Value replacedValue = destType.isInt1Type()
                    ? ConstantInt.getBool(val != 0, castInst.getModule())
                    : destType.isInt8Type()
                        ? ConstantInt.getChar(val & 0xff, castInst.getModule())
                        : ConstantInt.getInt(val, castInst.getModule());
            castInst.getUseList().stream()
                    .map(Use::getUser)
                    .forEach(worklist::add);
            castInst.replaceAllUseWith(replacedValue);
            castInst.eraseFromParent();
        }
    }

    private void foldBinary(Instruction inst) {
        // 重要!! 如果一个 inst 以及 eraseFromParent, 那它的 operand 和 parent 信息都被清掉了,
        // 但它可能还在 worklist 里, 如果不做这个判断, 往下进行会导致空指针异常!
        if (inst.getParent() == null) {
            return;
        }
        Value op1 = inst.getOperand(0);
        Value op2 = inst.getOperand(1);
        if (op1 instanceof ConstantInt c1 && op2 instanceof ConstantInt c2) {
            int val = switch (inst.getInstrType()) {
                case ADD -> c1.getValue() + c2.getValue();
                case SUB -> c1.getValue() - c2.getValue();
                case MUL -> c1.getValue() * c2.getValue();
                case SDIV -> c1.getValue() / c2.getValue();
                case SREM -> c1.getValue() % c2.getValue();
                case LT -> c1.getValue() < c2.getValue() ? 1 : 0;
                case LE -> c1.getValue() <= c2.getValue() ? 1 : 0;
                case GT -> c1.getValue() > c2.getValue() ? 1 : 0;
                case GE -> c1.getValue() >= c2.getValue() ? 1 : 0;
                case EQ -> c1.getValue() == c2.getValue() ? 1 : 0;
                case NE -> c1.getValue() != c2.getValue() ? 1 : 0;
                default -> throw new IllegalStateException("Unexpected value: " + inst.getInstrType());
            };
            Value replacedValue = inst instanceof IBinaryInst
                    ? ConstantInt.getInt(val, inst.getModule())
                    : ConstantInt.getBool(val != 0, inst.getModule());
            inst.getUseList().stream()
                    .map(Use::getUser)
                    .forEach(worklist::add);
            inst.replaceAllUseWith(replacedValue);
            inst.eraseFromParent();
        }
    }

    // todo: 常量折叠可以全局做, 公共子表达式还是基本块内做
}
