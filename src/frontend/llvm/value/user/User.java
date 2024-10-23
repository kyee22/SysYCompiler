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

package frontend.llvm.value.user;

import frontend.llvm.type.Type;
import frontend.llvm.value.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class User extends Value {
    private List<Value> operands;

    public User(Type type, String name) {
        super(type, name);
        this.operands = new ArrayList<>();
    }

    public List<Value> getOperands() {
        return operands;
    }

    public int getNumOperand() {
        return operands.size();
    }

    public Value getOperand(int index) {
        return operands.get(index);
    }

    public void setOperand(int index, Value value) {
        if (index >= operands.size()) {
            throw new IndexOutOfBoundsException("setOperand out of index");
        }
        Value oldValue = operands.get(index);
        if (oldValue != null) { // old operand
            oldValue.removeUse(this, index);
        }
        if (value != null) { // new operand
            value.addUse(this, index);
        }
        operands.set(index, value);
    }

    public void addOperand(Value value) {
        if (value == null) {
            throw new IllegalArgumentException("bad use: addOperand(null)");
        }
        value.addUse(this, operands.size());
        operands.add(value);
    }

    public void removeAllOperands() {
        for (int i = 0; i < operands.size(); ++i) {
            if (operands.get(i) != null) {
                operands.get(i).removeUse(this, i);
            }
        }
        operands.clear();
    }

    public void removeOperand(int index) {
        if (index >= operands.size()) {
            throw new IndexOutOfBoundsException("removeOperand out of index");
        }
        // 影响到其他的操作数
        for (int i = index + 1; i < operands.size(); ++i) {
            operands.get(i).removeUse(this, i);
            operands.get(i).addUse(this, i - 1);
        }
        // 移除指定的操作数
        operands.get(index).removeUse(this, index);
        operands.remove(index);
    }
}
