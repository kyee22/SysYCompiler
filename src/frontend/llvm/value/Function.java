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
import frontend.llvm.type.FunctionType;
import frontend.llvm.type.Type;
import frontend.llvm.value.user.instr.Instruction;
import midend.analysis.dataflow.analysis.DominationAnalysis;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.dataflow.fact.SetFact;

import java.util.*;
import java.util.stream.Collectors;

public class Function extends Value {
    private List<BasicBlock> basicBlocks = new ArrayList<>();
    private List<Argument> arguments = new ArrayList<>();
    private Module parent;
    private int seqCnt = 0;
    protected boolean isLeaf = true;

    private Function(FunctionType type, String name, Module parent) {
        super(type, name);
        parent.addFunction(this);
        this.parent = parent;
        for (int i = 0; i < type.getNumberOfArgs(); ++i) {
            arguments.add(new Argument(type.getParamType(i), "", this, i));
        }
    }

    public static Function create(FunctionType type, String name, Module parent) {return new Function(type, name, parent);}
    public FunctionType getFunctionType() {return (FunctionType) getType();}
    public Type getReturnType() {return getFunctionType().getReturnType();}
    public void addBasicBlock(BasicBlock bb) {basicBlocks.add(bb);}
    public int getNumArgs() {return arguments.size();}
    public int getNumBasicBlocks() {return basicBlocks.size();}
    public Module getParent() {return parent;}

    public void removeBasicBlock(BasicBlock bb) {
        basicBlocks.remove(bb);
        for (BasicBlock succ : bb.getSuccBasicBlocks()) {
            succ.removePrevBasicBlock(bb);
        }
        for (BasicBlock pred : bb.getPrevBasicBlocks()) {
            pred.removeSuccBasicBlock(bb);
        }
    }

    public BasicBlock getEntryBlock() {return basicBlocks.isEmpty() ? null : basicBlocks.get(0);}
    public List<BasicBlock> getBasicBlocks() {return basicBlocks;}
    public List<Argument> getArguments() {return arguments;}
    public boolean isDeclaration() {return basicBlocks.isEmpty();}

    public List<Instruction> getInstructions() {
        return getBasicBlocks().stream()
                .flatMap(bb -> bb.getInstrs().stream())
                .collect(Collectors.toList());
    }

    public void setInstrName() {
        Map<Value, Integer> seq = new HashMap<>();
        for (Argument arg : arguments) {
            if (!seq.containsKey(arg)) {
                int seqNum = seq.size() + seqCnt;
                arg.setName("arg" + seqNum);
                seq.put(arg, seqNum);
            }
        }
        for (BasicBlock bb : basicBlocks) {
            if (!seq.containsKey(bb)) {
                int seqNum = seq.size() + seqCnt;
                bb.setName("label" + seqNum);
                seq.put(bb, seqNum);
            }
            for (Instruction instr : bb.getInstrs()) {
                if (!instr.isVoid() && !seq.containsKey(instr)) {
                    int seqNum = seq.size() + seqCnt;
                    instr.setName("op" + seqNum);
                    seq.put(instr, seqNum);
                }
            }
        }
        seqCnt += seq.size();
    }

    @Override
    public String print() {
        setInstrName();
        StringBuilder funcIr = new StringBuilder();
        if (isDeclaration()) {
            funcIr.append("declare ");
        } else {
            funcIr.append("define ");
        }

        funcIr.append(getReturnType().print());
        funcIr.append(" ");
        funcIr.append(IRPrinter.printAsOp(this, false));
        funcIr.append("(");

        // Print args
        if (isDeclaration()) {
            for (int i = 0; i < getNumArgs(); i++) {
                if (i > 0) {
                    funcIr.append(", ");
                }
                funcIr.append(getFunctionType().getParamType(i).print());
            }
        } else {
            for (Argument arg : arguments) {
                if (arg != arguments.get(0)) {
                    funcIr.append(", ");
                }
                funcIr.append(arg.print());
            }
        }
        funcIr.append(")");

        // Print basic blocks
        if (isDeclaration()) {
            funcIr.append("\n");
        } else {
            funcIr.append(" {\n");
            for (BasicBlock bb : basicBlocks) {
                funcIr.append(bb.print()).append("\n");
            }
            funcIr.append("}");
        }

        return funcIr.toString();
    }

    public void setDomination(DataflowResult<BasicBlock, SetFact<BasicBlock>> result) {
        for (BasicBlock bb : getBasicBlocks()) {
            Set<BasicBlock> dominators = result.getOutFact(bb).stream().collect(Collectors.toSet());
            dominators.remove(bb);  //严格支配者不包含自己
            bb.setStrictDominators(dominators);
        }
        for (BasicBlock bb : getBasicBlocks()) {
            for (BasicBlock dominator : bb.getStrictDominators()) {
                boolean dominateOther = false;
                for (BasicBlock otherDominator : bb.getStrictDominators()) {
                    if (otherDominator.isStrictlyDominatedBy(dominator)) {
                        dominateOther = true;
                        break;
                    }
                }
                if (!dominateOther) {
                    bb.setImmediateDominator(dominator);
                    break;
                }
            }
        }
    }

    public void removeUnreachedInsts() {
        for (BasicBlock basicBlock : getBasicBlocks()) {
            basicBlock.removeUnreachedInsts();
        }
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setNonLeaf() {
        isLeaf = false;
    }
}
