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

package frontend.llvm.value;

import frontend.llvm.Module;
import frontend.llvm.value.user.instr.Instruction;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value {
    private Function parent;
    private List<Instruction> instructions = new ArrayList<>();

    private BasicBlock(Module module, String name, Function parent) {
        super(module.getLabelType(), "");
        if (parent == null) {
            throw new IllegalArgumentException("parent is null");
        }
        this.parent = parent;
        parent.addBasicBlock(this);
        //todo 还没完
    }

    public static BasicBlock create(Module module, String name, Function parent) {
        String prefix = name.isEmpty() ? "" : "label_";
        return new BasicBlock(module, prefix + name, parent);
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public String print() {
        // todo
        return null;
    }

    public Function getParent() {
        return parent;
    }

    public Module getModule() {
        return parent.getParent();
    }
}
