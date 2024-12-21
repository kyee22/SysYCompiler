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
import java.util.ArrayList;
import java.util.List;

public class PassManager {
    private Module module;
    private List<Pass> passes;

    public PassManager(Module module) {
        this.module = module;
        this.passes = new ArrayList<>();
    }

    public void addPass(Pass pass) {
        passes.add(pass);
    }

    public void run() {
        for (Pass pass : passes) {
            pass.run(module);
        }
    }
}
