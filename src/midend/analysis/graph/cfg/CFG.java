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

package midend.analysis.graph.cfg;

import java.util.Set;

public interface CFG<N> extends Iterable<N> {
    /**
     * @return the entry node of this CFG.
     */
    N getEntry();

    /**
     * @return the exit node of this CFG.
     */
    N getExit();

    /**
     * @return true if the given node is the entry of this CFG, otherwise false.
     */
    boolean isEntry(N node);

    /**
     * @return true if the given node is the exit of this CFG, otherwise false.
     */
    boolean isExit(N node);

    /**
     * @return incoming edges of the given node.
     */
    Set<Edge<N>> getInEdgesOf(N node);

    /**
     * @return outgoing edges of the given node.
     */
    Set<Edge<N>> getOutEdgesOf(N node);

    Set<N> getSuccsOf(N node);

    Set<N> getPredsOf(N node);

    public Set<N> getNodes();

    public boolean hasNode(N node);
}
