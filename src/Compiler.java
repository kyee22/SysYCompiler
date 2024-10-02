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

import frontend.core.InfoExtractVisitor;
import frontend.core.SemanticCheckVisitor;
import frontend.sysy.context.Context;
import utils.Charstream;
import frontend.core.Lexer;
import frontend.core.Parser;
import frontend.error.ErrorListener;
import frontend.error.ErrorReporter;

public class Compiler {
    private static final int LAB = 2;
    private static final String INPUT_PATH = "testfile.txt";
    private static final String LEXER_OUTPUT_PATH = "lexer.txt";
    private static final String ERR_PATH = "error.txt";
    private static final String PARSER_OUTPUT_PATH = "parser.txt";
    private static final String IR_PATH = "";   // todo
    private static final String ASM_PATH = "";  // todo

    public static void main(String[] args)  {
        /*      Error Handling is through the whole pipeline        */
        ErrorListener errorListener = new ErrorReporter();

        /*      STEP 0: Source File ---> Char Stream        */
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

        /*      Switch on output interface for corresponding lab    */
        switch (LAB) {
            case 1:
                lexer.flushTokens(LEXER_OUTPUT_PATH);
                break;
            case 2:
                InfoExtractVisitor infoExtractVisitor = new InfoExtractVisitor();
                ast.accept(infoExtractVisitor);
                infoExtractVisitor.flushInfos(PARSER_OUTPUT_PATH);
                break;
            default:
                break;
        }

        if (errorListener.hasErrors()) {
            errorListener.flushErrors(ERR_PATH);
        }

    }
}
