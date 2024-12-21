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
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.IBinaryInst;
import frontend.llvm.value.user.instr.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.StringUtils.sha1;


public class LocalVarNumbering implements Pass {
    private Map<String, Value> map;


    @Override
    public void run(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            run(basicBlock);
        }
    }

    private void run(BasicBlock basicBlock) {
        map = new HashMap<>();
        List<Instruction> deleteList = new ArrayList<>();
        for (Instruction inst: basicBlock.getInstrs()) {
            if (inst instanceof IBinaryInst binaryInst) {
                String hash = sha1(inst.getOperand(0).hashCode(), inst.getInstrType().hashCode(), inst.getOperand(1).hashCode());
                if (map.containsKey(hash)) {
                    inst.replaceAllUseWith(map.get(hash));
                    deleteList.add(inst);
                } else {
                    map.put(hash, binaryInst);
                    if (isInstrCommutative(binaryInst)) {
                        map.put(sha1(inst.getOperand(1).hashCode(), inst.getInstrType().hashCode(), inst.getOperand(0).hashCode()), binaryInst);
                    }
                }
            }
        }
        for (Instruction inst: deleteList) {
            inst.eraseFromParent();
        }
    }

    private static boolean isInstrCommutative(Instruction inst) {
        return inst.isAdd() || inst.isMul();
    }
}
