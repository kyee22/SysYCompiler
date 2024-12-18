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

package midend.analysis.dataflow.fact;

import frontend.llvm.value.user.instr.Instruction;

/**
 * An interface for querying data-flow results.
 *
 * @param <Node> type of graph nodes
 * @param <Fact> type of data-flow facts
 */
public interface NodeResult<Node, Fact>  {

    /**
     * @return the flowing-in fact of given node.
     */
    Fact getInFact(Node node);

    /**
     * @return the flowing-out fact of given node.
     */
    Fact getOutFact(Node node);

    default Fact getResult(Instruction inst) {
        return getOutFact((Node) inst);
    }
}
