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

import backend.core.ASMGenerator;
import frontend.core.*;
import frontend.sysy.context.Context;
import frontend.error.ErrorListener;
import frontend.error.ErrorReporter;
import midend.optimization.*;
import utils.Charstream;
import utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static int LAB = 5;
    private static final String INPUT_PATH = "testfile.txt";
    private static final String LEXER_OUTPUT_PATH = "lexer.txt";
    private static final String ERR_PATH = "error.txt";
    private static final String PARSER_OUTPUT_PATH = "parser.txt";
    private static final String SEMANTIC_CHECK_OUTPUT_PATH = "symbol.txt";
    private static final String IR_PATH = "llvm_ir.txt";
    private static final String ASM_PATH = "mips.txt";

    private static ErrorListener errorListener;
    private static Lexer lexer;
    private static Parser parser;
    private static Context ast;
    private static SemanticCheckVisitor semanticCheckVisitor;
    private static IRGenVisitor irGenVisitor;
    private static ASMGenerator asmGenerator;


    public static void main(String[] args)  {
        frontend();
        if (!errorListener.hasErrors()) {
            //midend();
            backend();
        }
        output();
    }

    private static void frontend() {
        /*      Error Handling is through the whole pipeline        */
        errorListener = new ErrorReporter();

        /*      STEP 0: Source File ---> Char Stream        */
        ((ErrorReporter) errorListener).read(INPUT_PATH);
        Charstream stream = Charstream.fromFile(INPUT_PATH);

        /*      STEP 1: Char Stream ---> Tokens        */
        lexer = new Lexer(stream);
        lexer.addErrorListener(errorListener);
        lexer.engine();

        /*      STEP 2: Tokens ---> AST        */
        parser = new Parser(lexer.getTokens());
        parser.addErrorListener(errorListener);
        parser.engine();
        ast = parser.getAst();

        /*      STEP 3: Semantic Check        */
        semanticCheckVisitor = new SemanticCheckVisitor();
        semanticCheckVisitor.addErrorListener(errorListener);
        ast.accept(semanticCheckVisitor);

        if (errorListener.hasErrors()) {
            System.out.println("Error detected! See " + ERR_PATH + " for details.");
            return;
        }

        /*      STEP 4: IR Gen        */
        irGenVisitor = new IRGenVisitor();
        ast.accept(irGenVisitor);
        irGenVisitor.getModule().removeUnreachedInsts();
    }

    private static void midend() {
        PassManager passManager = new PassManager(irGenVisitor.getModule());
        Pass[] passes = new Pass[]{
                new UnreachableBlockElim(),
                new Mem2RegPass(),
                new PhiElim(),
                new ConstantFolding(),
                //new LocalVarNumbering(),
                new DeadCodeDetect()
        };
        for (Pass pass : passes) {
            passManager.addPass(pass);
        }
        passManager.run();
    }

    private static void backend() {
        /*      STEP 5: Code Gen        */
        asmGenerator = new ASMGenerator();
        asmGenerator.genFrom(irGenVisitor.getModule());
    }

    private static void output() {
        if (errorListener.hasErrors()) {
            errorListener.flushErrors(ERR_PATH);
            return;
        }

        /*      Switch on output interface for corresponding lab    */
        switch (LAB) {
            case 1 -> lexer.flushTokens(LEXER_OUTPUT_PATH);
            case 2 -> {
                ASTDumpVisitor astDumpVisitor = new ASTDumpVisitor();
                ast.accept(astDumpVisitor);
                astDumpVisitor.flushInfos(PARSER_OUTPUT_PATH);
            }
            case 3 -> {
                List<String> records = new ArrayList<>();
                semanticCheckVisitor.getSymbolTables().stream().forEach(e -> records.addAll(e.getRecords()));
                FileUtils.writeListToFile(records, SEMANTIC_CHECK_OUTPUT_PATH);
            }
            case 4 -> irGenVisitor.dump(IR_PATH);
            case 5 -> asmGenerator.dump(ASM_PATH);
        }
    }
}
