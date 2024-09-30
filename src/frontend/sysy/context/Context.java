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

package frontend.sysy.context;

import java.util.List;
import java.util.ArrayList;


public class Context implements Visitable {
    private List<Context> children = new ArrayList<>();
    private Context parent = null;

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visitDefault(this);
    }

    public void add(Context context) {
        children.add(context);
        context.setParent(this);
    }

    public void add(TerminalContext ctx) {
        children.add(ctx);
    }

    public int getStartLineno() {
        return children.get(0).getStartLineno();
    }

    public int getEndLineno() {
        return children.get(children.size() - 1).getEndLineno();
    }

    public List<Context> getChildren() {
        return children;
    }

    public void setParent(Context parent) {
        this.parent = parent;
    }

    public Context getParent() {
        return parent;
    }
}

