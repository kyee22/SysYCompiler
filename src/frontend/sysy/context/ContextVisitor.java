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

public interface ContextVisitor<T> {
    default T visit(TerminalContext ctx) {return visitDefault(ctx);}
    default T visit(ProgramContext ctx) {return visitDefault(ctx);}
    default T visit(CompUnitContext ctx) {return visitDefault(ctx);}
    default T visit(DeclContext ctx) {return visitDefault(ctx);}
    default T visit(ConstDeclContext ctx) {return visitDefault(ctx);}
    default T visit(BTypeContext ctx) {return visitDefault(ctx);}
    default T visit(ConstDefContext ctx) {return visitDefault(ctx);}
    default T visit(ConstInitValContext ctx) {return visitDefault(ctx);}
    default T visit(VarDeclContext ctx) {return visitDefault(ctx);}
    default T visit(VarDefContext ctx) {return visitDefault(ctx);}
    default T visit(InitValContext ctx) {return visitDefault(ctx);}
    default T visit(FuncDefContext ctx) {return visitDefault(ctx);}
    default T visit(MainFuncDefContext ctx) {return visitDefault(ctx);}
    default T visit(FuncTypeContext ctx) {return visitDefault(ctx);}
    default T visit(FuncFParamContext ctx) {return visitDefault(ctx);}
    default T visit(FuncFParamsContext ctx) {return visitDefault(ctx);}
    default T visit(BlockContext ctx) {return visitDefault(ctx);}
    default T visit(BlockItemContext ctx) {return visitDefault(ctx);}
    default T visit(StmtContext ctx) {return visitDefault(ctx);}
    default T visit(AssignStmtContext ctx) {return visitDefault(ctx);}
    default T visit(IfStmtContext ctx) {return visitDefault(ctx);}
    default T visit(ForloopStmtContext ctx) {return visitDefault(ctx);}
    default T visit(ExpStmtContext ctx) {return visitDefault(ctx);}
    default T visit(BlockStmtContext ctx) {return visitDefault(ctx);}
    default T visit(BreakStmtContext ctx) {return visitDefault(ctx);}
    default T visit(ContinueStmtContext ctx) {return visitDefault(ctx);}
    default T visit(ReturnStmtContext ctx) {return visitDefault(ctx);}
    default T visit(GetIntStmtContext ctx ) {return visitDefault(ctx);}
    default T visit(GetCharStmtContext ctx) {return visitDefault(ctx);}
    default T visit(PrintfStmtContext ctx) {return visitDefault(ctx);}
    default T visit(ForStmtContext ctx) {return visitDefault(ctx);}
    default T visit(ExpContext ctx) {return visitDefault(ctx);}
    default T visit(CondContext ctx) {return visitDefault(ctx);}
    default T visit(LValContext ctx) {return visitDefault(ctx);}
    default T visit(PrimaryExpContext ctx) {return visitDefault(ctx);}
    default T visit(NumberContext ctx) {return visitDefault(ctx);}
    default T visit(CharacterContext ctx) {return visitDefault(ctx);}
    default T visit(UnaryExpContext ctx) {return visitDefault(ctx);}
    default T visit(UnaryOpContext ctx) {return visitDefault(ctx);}
    default T visit(FuncRParamsContext ctx) {return visitDefault(ctx);}
    default T visit(MulExpContext ctx) {return visitDefault(ctx);}
    default T visit(AddExpContext ctx) {return visitDefault(ctx);}
    default T visit(RelExpContext ctx) {return visitDefault(ctx);}
    default T visit(EqExpContext ctx) {return visitDefault(ctx);}
    default T visit(LAndExpContext ctx) {return visitDefault(ctx);}
    default T visit(LOrExpContext ctx) {return visitDefault(ctx);}
    default T visit(ConstExpContext ctx) {return visitDefault(ctx);}
    default T visitDefault(Context ctx) {return null;}
}
