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

import frontend.llvm.value.Function;

import java.util.*;
import java.util.stream.Collectors;

public class AbstractCFG<N> implements CFG<N> {
    protected N entry;
    protected N exit;
    protected final Set<N> nodes;
    private final Map<N, Set<Edge<N>>> inEdges;
    private final Map<N, Set<Edge<N>>> outEdges;

    AbstractCFG() {
        this.nodes = new HashSet<>();
        this.inEdges = new HashMap<>();
        this.outEdges = new HashMap<>();
    }

    void setEntry(N entry) {
        if (this.entry != null) {
            throw new IllegalStateException("CFG entry should be set only once");
        }

        this.entry = entry;
        this.nodes.add(entry);
    }

    @Override
    public N getEntry() {
        return this.entry;
    }

    void setExit(N exit) {
        if (this.exit != null) {
            throw new IllegalStateException("CFG exit should be set only once");
        }

        this.exit = exit;
        this.nodes.add(exit);
    }

    @Override
    public N getExit() {
        return this.exit;
    }

    @Override
    public boolean isEntry(N node) {
        return node == this.entry;
    }

    @Override
    public boolean isExit(N node) {
        return node == this.exit;
    }

    void addNode(N node) {
        this.nodes.add(node);
    }

    void addEdge(Edge<N> edge) {
        if (!inEdges.containsKey(edge.getTarget())) {
            inEdges.put(edge.getTarget(), new HashSet<>());
        }
        inEdges.get(edge.getTarget()).add(edge);
        if (!outEdges.containsKey(edge.getSource())) {
            outEdges.put(edge.getSource(), new HashSet<>());
        }
        outEdges.get(edge.getSource()).add(edge);
    }

    public Set<Edge<N>> getInEdgesOf(N node) {
        return this.inEdges.get(node);
    }

    public Set<Edge<N>> getOutEdgesOf(N node) {
        return this.outEdges.get(node);
    }

    @Override
    public boolean hasNode(N node) {
        return this.nodes.contains(node);
    }

    public boolean hasEdge(N source, N target) {
        return this.getSuccsOf(source).contains(target);
    }

    public Set<N> getPredsOf(N node) {
        if (!inEdges.containsKey(node)) {
            return Collections.emptySet();
        }

        return inEdges.get(node).stream()
                .map(Edge::getSource)
                .collect(Collectors.toSet());
    }

    public Set<N> getSuccsOf(N node) {
        if (!outEdges.containsKey(node)) {
            return Collections.emptySet();
        }

        return outEdges.get(node).stream()
                .map(Edge::getTarget)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<N> getNodes() {
        return Collections.unmodifiableSet(this.nodes);
    }

    @Override
    public Iterator<N> iterator() {
        return getNodes().iterator();
    }
}
