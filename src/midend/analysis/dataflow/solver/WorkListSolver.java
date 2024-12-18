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

package midend.analysis.dataflow.solver;

import midend.analysis.dataflow.analysis.DataflowAnalysis;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.graph.cfg.CFG;

import java.util.LinkedList;
import java.util.Queue;

class WorkListSolver<Node, Fact> extends Solver<Node, Fact> {

    WorkListSolver(DataflowAnalysis<Node, Fact> analysis) {
        super(analysis);
    }

    @Override
    protected void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me
        Queue<Node> worklist = new LinkedList<>();
        worklist.addAll(cfg.getNodes());

        while (!worklist.isEmpty()) {
            Node node = worklist.poll();
            if (cfg.isEntry(node)) {
                continue;
            }

            // IN[B] = ⊔ OUT[P] (P a predecessor of B);
            for (Node pred : cfg.getPredsOf(node)) {
                analysis.meetInto(result.getOutFact(pred), result.getInFact(node));
            }
            // OUT[B] = gen_B U (IN[B] - kill_B);
            if (analysis.transferNode(node, result.getInFact(node), result.getOutFact(node))) {
                worklist.addAll(cfg.getSuccsOf(node));
            }
        }
    }

    @Override
    protected void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        Queue<Node> worklist = new LinkedList<>();
        worklist.addAll(cfg.getNodes());

        while (!worklist.isEmpty()) {
            Node node = worklist.poll();
            if (cfg.isExit(node)) {
                continue;
            }
            for (Node succ : cfg.getSuccsOf(node)) {
                analysis.meetInto(result.getInFact(succ), result.getOutFact(node));
            }
            if (analysis.transferNode(node, result.getInFact(node), result.getOutFact(node))) {
                worklist.addAll(cfg.getPredsOf(node));
            }
        }
    }
}
