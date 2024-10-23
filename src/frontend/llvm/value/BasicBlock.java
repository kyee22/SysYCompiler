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

import frontend.llvm.IRPrinter;
import frontend.llvm.Module;
import frontend.llvm.value.user.instr.Instruction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BasicBlock extends Value {
    private Function parent;
    private List<Instruction> instructions = new LinkedList<>();
    private List<BasicBlock> prevBasicBlocks = new ArrayList<>();
    private List<BasicBlock> succBasicBlocks = new ArrayList<>();

    private BasicBlock(Module module, String name, Function parent) {
        super(module.getLabelType(), name);
        if (parent == null) {
            throw new IllegalArgumentException("parent is null");
        }
        this.parent = parent;
        parent.addBasicBlock(this);
    }

    public static BasicBlock create(Module module, String name, Function parent) {
        String prefix = name.isEmpty() ? "" : "label_";
        return new BasicBlock(module, prefix + name, parent);
    }

    /******************************** api about cfg ********************************/
    public void addPrevBasicBlock(BasicBlock prevBasicBlock) {prevBasicBlocks.add(prevBasicBlock);}
    public void addSuccBasicBlock(BasicBlock succBasicBlock) {succBasicBlocks.add(succBasicBlock);}
    public void removePrevBasicBlock(BasicBlock basicBlock) {prevBasicBlocks.remove(basicBlock);}
    public void removeSuccBasicBlock(BasicBlock basicBlock) {succBasicBlocks.remove(basicBlock);}
    public List<BasicBlock> getPrevBasicBlocks() {return prevBasicBlocks;}
    public List<BasicBlock> getSuccBasicBlocks() {return succBasicBlocks;}

    /******************************** api about Instruction ********************************/
    public void addInstr(Instruction instruction) {instructions.add(instruction);}
    public void addInstrBegin(Instruction instruction) {instructions.add(0, instruction);}
    public void remoteInstr(Instruction instruction) {instructions.remove(instruction);}
    public List<Instruction> getInstrs() {return instructions;}
    public boolean isEmpty() {return instructions.isEmpty();}
    public int getNumOfInstr() {return instructions.size();}

    /******************************** api about accessing parent ********************************/
    public Function getParent() {return parent;}
    public Module getModule() {return getParent().getParent();}
    public void eraseFromParent() {getParent().removeBasicBlock(this);}


    // If the Block is terminated by ret/br
    public boolean isTerminated() {
        if (isEmpty()) {
            return false;
        }
        return instructions.get(getNumOfInstr() - 1).isTerminator();
    }

    // Get terminator, only accept valid case use
    public Instruction getTerminator() {
        if (!isTerminated()) {
            throw new IllegalStateException("Trying to get terminator from an bb which is not terminated");
        }
        return instructions.get(getNumOfInstr() - 1);
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(':');
        if (!getPrevBasicBlocks().isEmpty()) {
            sb.append("                                                ; preds = ");
            for (int i = 0; i < getPrevBasicBlocks().size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(IRPrinter.printAsOp(getPrevBasicBlocks().get(i), false));
            }
        }

        if (getParent() == null) {
            sb.append("\n");
            sb.append("; Error: Block without parent!");
        }

        sb.append("\n");
        for (Instruction instr : getInstrs()) {
            sb.append("  ");
            sb.append(instr.print());
            sb.append("\n");
        }
        return sb.toString();
    }
}
