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

import static utils.AssertUtils.ASSERT;

public class StmtContext extends Context {
    private AssignStmtContext assignStmt = null;
    private ExpStmtContext expStmt = null;
    private BlockStmtContext blockStmt = null;
    private IfStmtContext ifStmt = null;
    private ForloopStmtContext forloopStmt = null;
    private BreakStmtContext breakStmt = null;
    private ContinueStmtContext continueStmt = null;
    private ReturnStmtContext returnStmt = null;
    private GetIntStmtContext getIntStmt = null;
    private GetCharStmtContext getCharStmt = null;
    private PrintfStmtContext printfStmt = null;


    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof AssignStmtContext) {
            assignStmt = (AssignStmtContext) context;
        } else if (context instanceof ExpStmtContext) {
            expStmt = (ExpStmtContext) context;
        } else if (context instanceof BlockStmtContext) {
            blockStmt = (BlockStmtContext) context;
        } else if (context instanceof IfStmtContext) {
            ifStmt = (IfStmtContext) context;
        } else if (context instanceof ForloopStmtContext) {
            forloopStmt = (ForloopStmtContext) context;
        } else if (context instanceof BreakStmtContext) {
            breakStmt = (BreakStmtContext) context;
        } else if (context instanceof ContinueStmtContext) {
            continueStmt = (ContinueStmtContext) context;
        } else if (context instanceof ReturnStmtContext) {
            returnStmt = (ReturnStmtContext) context;
        } else if (context instanceof GetIntStmtContext) {
            getIntStmt = (GetIntStmtContext) context;
        } else if (context instanceof GetCharStmtContext) {
            getCharStmt = (GetCharStmtContext) context;
        } else if (context instanceof PrintfStmtContext) {
            printfStmt = (PrintfStmtContext) context;
        } else {
            ASSERT(false, "Stmt only accepts AssignStmtContext, ExpStmtContext, BlockStmtContext, IfStmtContext, ForloopStmtContext, BreakStmtContext, ContinueStmtContext, ReturnStmtContext, GetIntStmtContext, GetCharStmtContext, PrintfStmtContext);");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        ASSERT(false, "Stmt only accepts Non-Terminal Context");
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
