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

package midend.analysis.graph;

import midend.analysis.graph.cfg.CFG;

public interface CFGWalker<Node> {
    void bfs(CFG<Node> cfg);

    void dfs(CFG<Node> cfg);

    public abstract void enterStaff(Node node);

    public abstract void leaveStaff(Node node);
}
