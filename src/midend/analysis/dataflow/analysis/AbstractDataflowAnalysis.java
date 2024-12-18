/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package midend.analysis.dataflow.analysis;


import frontend.llvm.value.Function;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.dataflow.solver.Solver;
import midend.analysis.graph.cfg.CFG;
import midend.analysis.graph.cfg.CFGBuilder;
import midend.analysis.graph.cfg.Edge;
import midend.analysis.graph.cfg.InstCFGBuilder;

public abstract class AbstractDataflowAnalysis<Node, Fact>
        implements DataflowAnalysis<Node, Fact> {

    private final Solver<Node, Fact> solver;
    private CFG<Node> cfg;
    private CFGBuilder cfgBuilder;
    protected Function function;

    protected AbstractDataflowAnalysis(CFGBuilder cfgBuilder) {
        this.cfgBuilder = cfgBuilder;
        solver = Solver.makeSolver(this);
    }

    public DataflowResult<Node, Fact> analyze(Function function) {
        this.function = function;
        cfg = cfgBuilder.analyze(function);
        return solver.solve(cfg);
    }

    /**
     * By default, a data-flow analysis does not have edge transfer, i.e.,
     * does not need to perform transfer for any edges.
     */
    @Override
    public boolean needTransferEdge(Edge<Node> edge) {
        return false;
    }

    @Override
    public Fact transferEdge(Edge<Node> edge, Fact nodeFact) {
        throw new UnsupportedOperationException();
    }

    public CFG<Node> getCfg() {return cfg;}
}
