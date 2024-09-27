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

import frontend.core.AstExtractVisitor;
import frontend.sysy.context.Context;
import utils.Charstream;
import frontend.core.Lexer;
import frontend.core.Parser;
import frontend.error.ErrorListener;
import frontend.error.ErrorReporter;

import static utils.AssertUtils.ASSERT;

public class Compiler {
    private static final String INPUT_PATH = "testfile.txt";
    private static final String LEXER_OUTPUT_PATH = "lexer.txt";
    private static final String ERR_PATH = "error.txt";
    private static final String PARSER_OUTPUT_PATH = "parser.txt";  // todo
    private static final String IR_PATH = "";   // todo
    private static final String ASM_PATH = "";  // todo

    public static void main(String[] args)  {
        /*      Error Handle, which is through the whole pipeline        */
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
        if (errorListener.hasErrors()) {
            errorListener.flushErrors(ERR_PATH);
        } else {
            AstExtractVisitor visitor = new AstExtractVisitor();
            Context ast = parser.getAst();
            ast.accept(visitor);
            visitor.flushInfos(PARSER_OUTPUT_PATH);
        }
    }
}
