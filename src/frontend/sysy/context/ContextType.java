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

public enum ContextType {
    CompUnit,
    Decl,
    ConstDecl, BType, ConstDef, ConstInitVal,
    VarDecl, VarDef, InitVal,
    FuncDef, MainFuncDef,
    FuncType, FuncFParams, FuncFParam,
    Block, BlockItem,
    Stmt, AssignStmt, ExpStmt, BlockStmt, IfStmt, ForloopStmt, BreakStmt,
    ContinueStmt, ReturnStmt, GetIntStmt, GetCharStmt, PrintfStmt,
    ForStmt,
    Exp,
    Cond,
    LVal,
    PrimaryExp,
    Number, Character,
    UnaryExp, UnaryOp,
    FuncRParams,
    MulExp, AddExp, RelExp, EqExp, LAndExp, LOrExp,
    ConstExp
    ;

}
