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

    /*  TODO: 一些低效生成的 ir
     *      1) 条件判断反复 trunc 与 zext
     *      2) 分支条件判断产生巨量 br
     *    不一定所以指令都要临时寄存器:
     *      1) resolve arg
     */

    private static void pipeline() {
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
        irGenVisitor.dump(IR_PATH);
        irGenVisitor.getModule().removeUnreachedInsts();


        // 对于从 entry 出发无法到达的结点，讨论其支配关系是没有意义的
        // 所以我们先删掉无法到达的节点
        (new UnreachableBlockElim()).run(irGenVisitor.getModule());
        (new Mem2RegPass()).run(irGenVisitor.getModule());
        (new PhiElim()).run(irGenVisitor.getModule());
        (new ConstantFolding()).run(irGenVisitor.getModule());
        //(new LocalVarNumbering()).run(irGenVisitor.getModule());
        (new DeadCodeDetect()).run(irGenVisitor.getModule());

        /*      STEP 5: Code Gen        */
        asmGenerator = new ASMGenerator();
        asmGenerator.genFrom(irGenVisitor.getModule());
    }

    private static void output() {

        if (errorListener.hasErrors()) {
            errorListener.flushErrors(ERR_PATH);
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
    // todo: mem2reg之后会产生新的 compile-time constant
    //       但此时 IR 生成已经结束,

    public static void main(String[] args)  {
        pipeline();
        output();
    }
}

//InstCFGBuilder builder = new InstCFGBuilder();
//String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//FileUtils.makeDir("livevar/" + timestamp);
//for (Function function : irGenVisitor.getModule().getFunctions()) {
//    if (function.isDeclaration()) {
//        continue;
//    }
//    function.setInstrName();
//    StringBuilder sb = new StringBuilder();
//    LiveVariableAnalysis analysis = new LiveVariableAnalysis();
//    DataflowResult<Instruction, SetFact<Value>> result = analysis.analyze(function);
//    sb.append(String.format("================== %s liveVar ================\n", function.getName()));
//    for (BasicBlock bb : function.getBasicBlocks()) {
//        sb.append(bb.getName() + ":\n");
//        for (Instruction inst : bb.getInstrs()) {
//            sb.append("\t@" + inst.getIndex() + ": ");
//            sb.append(inst.print());
//            sb.append("  " + result.getResult(inst).toString() + "\n");
//        }
//        sb.append("\n");
//    }
//    FileUtils.writeStringToFile("livevar/" + timestamp + "/" + function.getName() + "-livevar.txt", sb.toString());
//}