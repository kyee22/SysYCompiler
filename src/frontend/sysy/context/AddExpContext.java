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

import static frontend.sysy.token.TokenType.MINU;
import static frontend.sysy.token.TokenType.PLUS;
import static utils.AssertUtils.ASSERT;

import java.util.ArrayList;
import java.util.List;

public class AddExpContext extends Context {
    private List<TerminalContext> OP_ = new ArrayList<>();
    private List<MulExpContext> mulExp = new ArrayList<>();

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof MulExpContext) {
            mulExp.add((MulExpContext) context);
        } else {
            ASSERT(false, "AddExp only accepts MulExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().any(PLUS, MINU)) {
            OP_.add(ctx);
        } else {
            ASSERT(false, "AddExp only accepts PLUS and MINU");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public List<MulExpContext> mulExp() {
        return mulExp;
    }

    public MulExpContext mulExp(int index) {
        return mulExp.get(index);
    }

    public List<TerminalContext> OP() {
        return OP_;
    }

    public TerminalContext OP(int index) {
        return OP_.get(index);
    }
}
