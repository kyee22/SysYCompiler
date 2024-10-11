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

import java.util.*;

import static utils.Charstream.*;
import static frontend.sysy.token.TokenType.*;
import static frontend.sysy.token.Token.*;
import static utils.FileUtils.writeListToFile;
import static frontend.error.ErrorType.*;

import utils.Charstream;
import frontend.error.ErrorListener;
import frontend.error.ErrorType;
import frontend.sysy.token.Token;
import frontend.sysy.token.TokenType;

public class Lexer {

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        /*      keywords       */
        keywords.put("main", MAINTK);
        keywords.put("const", CONSTTK);
        keywords.put("int", INTTK);
        keywords.put("char", CHARTK);
        keywords.put("for", FORTK);
        keywords.put("if", IFTK);
        keywords.put("else", ELSETK);
        keywords.put("break", BREAKTK);
        keywords.put("continue", CONTINUETK);
        keywords.put("return", RETURNTK);
        keywords.put("void", VOIDTK);
        /*      i/o interfaces (viewed as keywords)       */
        keywords.put("getint", GETINTTK);
        keywords.put("getchar", GETCHARTK);
        keywords.put("printf", PRINTFTK);
    }

    private Charstream stream;
    private int lineno = 1;
    private int lineStartOffset = -1;
    private List<Token> tokens = new ArrayList<>();
    private List<ErrorListener> errorListeners = new ArrayList<>();

    public Lexer(Charstream charstream) {
        this.stream = charstream;
    }

    public void engine() {
        Token token;
        while ((token = next()) != null && !token.is(EOFTK)) {
            tokens.add(token);
        }
    }

    private Token next() {
        StringBuilder builder = new StringBuilder();
        int colno = stream.getPos() - lineStartOffset;
        char ch = stream.getc();
        builder.append(ch);

        if (Character.isAlphabetic(ch) || ch == '_') {
            ch = stream.getc();
            while (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_') {
                builder.append(ch);
                ch = stream.getc();
            }
            stream.ungetc();
            String text = builder.toString();
            TokenType type = keywords.containsKey(text) ? keywords.get(text) : IDENFR;
            return makeToken(type, text, lineno, colno);
        } else if (Character.isDigit(ch)) {
            if (ch != '0') {
                ch = stream.getc();
                while (Character.isDigit(ch)) {
                    builder.append(ch);
                    ch = stream.getc();
                }
                stream.ungetc();
            }
            return makeToken(INTCON, builder.toString(), lineno, colno);
        } else if (ch == '\"') {
            ch = stream.getc();
            while (ch != '\"') {
                if (ch == '\\') {
                    builder.append(ch);
                    ch = stream.getc();
                }
                builder.append(ch);
                ch = stream.getc();
            }
            builder.append(ch);
            return makeToken(STRCON, builder.toString(), lineno, colno);
        } else if (ch == '\'') {
            // todo
            ch = stream.getc();
            builder.append(ch);
            if (ch == '\\') {
                ch = stream.getc();
                builder.append(ch);
            }
            ch = stream.getc();
            builder.append(ch);
            return makeToken(CHRCON, builder.toString(), lineno, colno);
        } else if (ch == '+') {
            return makeToken(PLUS, builder.toString(), lineno, colno);
        } else if (ch == '-') {
            return makeToken(MINU, builder.toString(), lineno, colno);
        } else if (ch == '*') {
            return makeToken(MULT, builder.toString(), lineno, colno);
        } else if (ch == '/') {
            ch = stream.getc();
            if (ch == '/') {
                while (ch != '\n' && ch != EOF) {
                    ch = stream.getc();
                }
                stream.ungetc();
                return next();
            } else if (ch == '*') {
                ch = stream.getc();
                while (ch != EOF) {
                    if (ch == '\n') {
                        newLine();
                    }
                    if (ch == '*') {
                        ch = stream.getc();
                        if (ch == '/') {
                            break;
                        } else {
                            stream.ungetc();
                        }
                    }
                    ch = stream.getc();
                }
                assert ch == '/';
                return next();
            }
            stream.ungetc();
            return makeToken(DIV, builder.toString(), lineno, colno);
        } else if (ch == '%') {
            return makeToken(MOD, builder.toString(), lineno, colno);
        } else if (ch == '!') {
            ch = stream.getc();
            if (ch == '=') {
                builder.append(ch);
                return makeToken(NEQ, builder.toString(), lineno, colno);
            } else {
                stream.ungetc();
                return makeToken(NOT, builder.toString(), lineno, colno);
            }
        } else if (ch == '&') {
            ch = stream.getc();
            if (ch == '&') {
                builder.append(ch);
            } else {
                stream.ungetc();
                notifyErrorListeners(lineno, colno, ILLEGAL_SYM);
            }
            return makeToken(AND, builder.toString(), lineno, colno);
        } else if (ch == '|') {
            ch = stream.getc();
            if (ch == '|') {
                builder.append(ch);
            } else {
                stream.ungetc();
                notifyErrorListeners(lineno, colno, ILLEGAL_SYM);
            }
            return makeToken(OR, builder.toString(), lineno, colno);
        } else if (ch == '<') {
            ch = stream.getc();
            if (ch == '=') {
                builder.append(ch);
                return makeToken(LEQ, builder.toString(), lineno, colno);
            } else {
                stream.ungetc();
                return makeToken(LSS, builder.toString(), lineno, colno);
            }
        } else if (ch == '>') {
            ch = stream.getc();
            if (ch == '=') {
                builder.append(ch);
                return makeToken(GEQ, builder.toString(), lineno, colno);
            } else {
                stream.ungetc();
                return makeToken(GRE, builder.toString(), lineno, colno);
            }
        } else if (ch == '=') {
            ch = stream.getc();
            if (ch == '=') {
                builder.append(ch);
                return makeToken(EQL, builder.toString(), lineno, colno);
            } else {
                stream.ungetc();
                return makeToken(ASSIGN, builder.toString(), lineno, colno);
            }
        } else if (ch == ',') {
            return makeToken(COMMA, builder.toString(), lineno, colno);
        } else if (ch == ';') {
            return makeToken(SEMICN, builder.toString(), lineno, colno);
        } else if (ch == '(') {
            return makeToken(LPARENT, builder.toString(), lineno, colno);
        } else if (ch == ')') {
            return makeToken(RPARENT, builder.toString(), lineno, colno);
        } else if (ch == '[') {
            return makeToken(LBRACK, builder.toString(), lineno, colno);
        } else if (ch == ']') {
            return makeToken(RBRACK, builder.toString(), lineno, colno);
        } else if (ch == '{') {
            return makeToken(LBRACE, builder.toString(), lineno, colno);
        } else if (ch == '}') {
            return makeToken(RBRACE, builder.toString(), lineno, colno);
        } else if (ch == '\n') {
            newLine();
            return next();
        } else if (Character.isWhitespace(ch)) {
            return next();
        } else if (ch == EOF) {
            return makeToken(EOFTK, "EOF", lineno, colno);
        } else {
            return makeToken(UNDEF, builder.toString(), lineno, colno);
        }
    }
    
    private void newLine() {
        ++lineno;
        lineStartOffset = stream.getPos() - 1;
    }

    public void flushTokens(String filePath) {
        writeListToFile(tokens, filePath);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    private void notifyErrorListeners(int lineno, int colno, ErrorType errorType) {
        for (ErrorListener listener : errorListeners) {
            listener.onError(lineno, colno, errorType);  // 通知所有监听者
        }
    }
}
