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

import frontend.core.*;
import frontend.sysy.context.Context;
import frontend.error.ErrorListener;
import frontend.error.ErrorReporter;
import utils.Charstream;
import utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static int LAB = 3;
    private static final String INPUT_PATH = "testfile.txt";
    private static final String LEXER_OUTPUT_PATH = "lexer.txt";
    private static final String ERR_PATH = "error.txt";
    private static final String PARSER_OUTPUT_PATH = "parser.txt";
    private static final String SEMANTIC_CHECK_OUTPUT_PATH = "symbol.txt";
    private static final String IR_PATH = "";   // todo
    private static final String ASM_PATH = "";  // todo

    public static void main(String[] args)  {
        /*      Error Handling is through the whole pipeline        */
        ErrorListener errorListener = new ErrorReporter();

        /*      STEP 0: Source File ---> Char Stream        */
        ((ErrorReporter) errorListener).read(INPUT_PATH);
        Charstream stream = Charstream.fromFile(INPUT_PATH);

        /*      STEP 1: Char Stream ---> Tokens        */
        Lexer lexer = new Lexer(stream);
        lexer.addErrorListener(errorListener);
        lexer.engine();

        /*      STEP 2: Tokens ---> AST        */
        Parser parser = new Parser(lexer.getTokens());
        parser.addErrorListener(errorListener);
        parser.engine();
        Context ast = parser.getAst();

        /*      STEP 3: Semantic Check        */
        SemanticCheckVisitor semanticCheckVisitor = new SemanticCheckVisitor();
        semanticCheckVisitor.addErrorListener(errorListener);
        ast.accept(semanticCheckVisitor);

        /*      STEP 4: IR Gen        */
        IRGenVisitor irGenVisitor = new IRGenVisitor();
        ast.accept(irGenVisitor);

        /*      Switch on output interface for corresponding lab    */
        switch (LAB) {
            case 1:
                lexer.flushTokens(LEXER_OUTPUT_PATH);
                break;
            case 2:
                ASTDumpVisitor astDumpVisitor = new ASTDumpVisitor();
                ast.accept(astDumpVisitor);
                astDumpVisitor.flushInfos(PARSER_OUTPUT_PATH);
                break;
            case 3:
                List<String> records = new ArrayList<>();
                semanticCheckVisitor.getSymbolTables().stream().forEach(e -> records.addAll(e.getRecords()));
                FileUtils.writeListToFile(records, SEMANTIC_CHECK_OUTPUT_PATH);
                break;
            case 4:
                // todo
                break;
            default:
                break;
        }

        if (errorListener.hasErrors()) {
            //System.out.println("Errors detected");
            errorListener.flushErrors(ERR_PATH);
        }

        //((ErrorReporter) errorListener).hint();
    }
}
