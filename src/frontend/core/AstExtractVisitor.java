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

package frontend.core;

import frontend.sysy.context.*;
import static utils.FileUtils.writeListToFile;

import java.util.ArrayList;
import java.util.List;

public class AstExtractVisitor extends BaseContextVisitor<Void> {
    private List<String> infos = new ArrayList<>();

    public void flushInfos(String filePath) {
        writeListToFile(infos, filePath);
    }

    @Override
    public Void visit(TerminalContext ctx) {
        infos.add(ctx.getToken().toString());
        return null;
    }

    @Override
    public Void visit(CompUnitContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<CompUnit>");
        return r;
    }

    @Override
    public Void visit(DeclContext ctx) {
        return super.visit(ctx);
    }

    @Override
    public Void visit(ConstDeclContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<ConstDecl>");
        return r;
    }

    @Override
    public Void visit(BTypeContext ctx) {
        return super.visit(ctx);
    }

    @Override
    public Void visit(ConstDefContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<ConstDef>");
        return r;
    }

    @Override
    public Void visit(ConstInitValContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<ConstInitVal>");
        return r;
    }

    @Override
    public Void visit(VarDeclContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<VarDecl>");
        return r;
    }

    @Override
    public Void visit(VarDefContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<VarDef>");
        return r;
    }

    @Override
    public Void visit(InitValContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<InitVal>");
        return r;
    }

    @Override
    public Void visit(FuncDefContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<FuncDef>");
        return r;
    }

    @Override
    public Void visit(MainFuncDefContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<MainFuncDef>");
        return r;
    }

    @Override
    public Void visit(FuncTypeContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<FuncType>");
        return r;
    }

    @Override
    public Void visit(FuncFParamContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<FuncFParam>");
        return r;
    }

    @Override
    public Void visit(FuncFParamsContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<FuncFParams>");
        return r;
    }

    @Override
    public Void visit(BlockContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<Block>");
        return r;
    }

    @Override
    public Void visit(BlockItemContext ctx) {
        return super.visit(ctx);
    }

    @Override
    public Void visit(StmtContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<Stmt>");
        return r;
    }

    @Override
    public Void visit(ForStmtContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<ForStmt>");
        return r;
    }

    @Override
    public Void visit(ExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<Exp>");
        return r;
    }

    @Override
    public Void visit(CondContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<Cond>");
        return r;
    }

    @Override
    public Void visit(LValContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<LVal>");
        return r;
    }

    @Override
    public Void visit(PrimaryExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<PrimaryExp>");
        return r;
    }

    @Override
    public Void visit(NumberContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<Number>");
        return r;
    }

    @Override
    public Void visit(UnaryExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<UnaryExp>");
        return r;
    }

    @Override
    public Void visit(CharacterContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<Character>");
        return r;
    }

    @Override
    public Void visit(UnaryOpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<UnaryOp>");
        return r;
    }

    @Override
    public Void visit(FuncRParamsContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<FuncRParams>");
        return r;
    }

    @Override
    public Void visit(MulExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<MulExp>");
        return r;
    }

    @Override
    public Void visit(AddExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<AddExp>");
        return r;
    }

    @Override
    public Void visit(RelExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<RelExp>");
        return r;
    }

    @Override
    public Void visit(EqExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<EqExp>");
        return r;
    }

    @Override
    public Void visit(LOrExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<LOrExp>");
        return r;
    }

    @Override
    public Void visit(LAndExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<LAndExp>");
        return r;
    }

    @Override
    public Void visit(ConstExpContext ctx) {
        Void r = super.visit(ctx);
        infos.add("<ConstExp>");
        return r;
    }
}
