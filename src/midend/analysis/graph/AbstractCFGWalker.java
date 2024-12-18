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

import java.util.*;

public abstract class AbstractCFGWalker<Node> implements CFGWalker<Node> {
    private Set<Node> visited;
    private CFG<Node> cfg;

    @Override
    public void bfs(CFG<Node> cfg) {
        Set<Node> visited = new HashSet<>();
        Queue<Node> worklist = new LinkedList<>();
        worklist.offer(cfg.getEntry());

        while (!worklist.isEmpty()) {
            Node node = worklist.poll();
            if (!visited.add(node)) {
                continue;
            }

            enterStaff(node);
            leaveStaff(node);

            for (Node succ : cfg.getSuccsOf(node)) {
                if (!visited.contains(succ)) {
                    worklist.offer(succ);
                }
            }
        }
    }


    @Override
    public void dfs(CFG<Node> cfg) {
        visited = new HashSet<>();
        this.cfg = cfg;
        doDfs(cfg.getEntry());
    }

    private void doDfs(Node node) {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);
        enterStaff(node);
        for (Node succ : cfg.getSuccsOf(node)) {
            if (!visited.contains(succ)) {
                doDfs(succ);
            }
        }
        leaveStaff(node);
    }

    @Override
    public abstract void enterStaff(Node node);

    @Override
    public abstract void leaveStaff(Node node);
}
