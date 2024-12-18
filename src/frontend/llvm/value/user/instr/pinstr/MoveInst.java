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

package frontend.llvm.value.user.instr.pinstr;

import frontend.llvm.IRPrinter;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.InstVisitor;
import frontend.llvm.value.user.instr.Instruction;

import java.util.List;
import java.util.Optional;


public class MoveInst extends Instruction {
    private MoveInst(Value source, Value target, BasicBlock bb) {
        super(bb.getModule().getVoidType(), OpID.PSEUDO_MOVE, bb);
        if (target.getType() != source.getType()) {
            throw new IllegalArgumentException("MoveInst: target must be the same type as source");
        }
        addOperand(source);
        addOperand(target);
    }

    public static MoveInst createMove(Value source, Value target, BasicBlock bb) {
        return new MoveInst(source, target, bb);
    }

    @Override
    public String print() {
        StringBuilder instrIr = new StringBuilder();

        instrIr.append(getInstrOpName())
                .append(" ")
                .append(getOperand(0).getType().print())
                .append(" ")
                .append(IRPrinter.printAsOp(getOperand(0), false))
                .append(" to ")
                .append(IRPrinter.printAsOp(getOperand(1), false));

        return instrIr.toString();
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}

    /******************** 活跃变量分析时用到的 api ********************/
    @Override
    public Optional<Value> getDef() {
        // 按照基类的方式, isVoid 的 MoveInst 应该没有定义变量
        // 但事实上 move 这个动作的语义蕴含了对 target 的定义.
        // 此外, 消除 phi 之后, 全局将不存在对 target 的显示定义 (即 %target = ... 的形式)
        // 若不在在此处指明定义从而杀死定义, target 的活跃区间显然会爆炸增长.
        return Optional.of(getOperand(1));
    }

    @Override
    public List<Value> getUses() {
        if (getOperand(0) instanceof Instruction) {
            return List.of(getOperand(0));
        } else {
            return List.of();
        }
    }
}
