package frontend;

import frontend.core.Lexer;
import frontend.sysy.token.Token;
import org.junit.Test;
import utils.Charstream;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {

    @Test
    public void testIntconCorner1() {
        Charstream s = Charstream.fromString("0123456789");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{"INTCON 0", "INTCON 123456789", "EOFTK EOF"};
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testIntconCorner2() {
        Charstream s = Charstream.fromString("0009877");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{"INTCON 0", "INTCON 0", "INTCON 0", "INTCON 9877"};
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testIntconCorner3() {
        Charstream s = Charstream.fromString("0123SysYCompiler0123");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{"INTCON 0", "INTCON 123",  "IDENFR SysYCompiler0123"};
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testStrCorner() {
        Charstream s = Charstream.fromString("\"\\\"01\\\t23SysY\\\"\\\"Com\\\"il\\\"er0123\\\"\"");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{"STRCON \"\\\"01\\\t23SysY\\\"\\\"Com\\\"il\\\"er0123\\\"\""};
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }


    @Test
    public void testChrconCorner() {
        Charstream s = Charstream.fromString("'a' '1' '{' '%' '~' '\\t' '\\n'");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{"CHRCON 'a'", "CHRCON '1'",  "CHRCON '{'", "CHRCON '%'", "CHRCON '~'", "CHRCON '\\t'", "CHRCON '\\n'"};
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testKeywords() {
        Charstream s = Charstream.fromString("main const int char for if else break continue return void getint getchar printf");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{
                "MAINTK main", "CONSTTK const", "INTTK int", "CHARTK char",
                "FORTK for", "IFTK if", "ELSETK else", "BREAKTK break",
                "CONTINUETK continue", "RETURNTK return", "VOIDTK void",
                "GETINTTK getint", "GETCHARTK getchar", "PRINTFTK printf"
        };
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testOperators() {
        Charstream s = Charstream.fromString("+ - * / % && || == != < <= > >= =");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{
                "PLUS +", "MINU -", "MULT *", "DIV /", "MOD %",
                "AND &&", "OR ||", "EQL ==", "NEQ !=",
                "LSS <", "LEQ <=", "GRE >", "GEQ >=", "ASSIGN ="
        };
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testStringConstant() {
        Charstream s = Charstream.fromString("\"Hello, World!\" \"Test String with 123\"");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{
                "STRCON \"Hello, World!\"", "STRCON \"Test String with 123\""
        };
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testComments() {
        Charstream s = Charstream.fromString("// this is a single-line comment\nint a = 5; /* multi-line comment */");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{
                "INTTK int", "IDENFR a", "ASSIGN =", "INTCON 5", "SEMICN ;"
        };
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testMixedInputs() {
        Charstream s = Charstream.fromString("int x = 10; if (x > 5) printf(\"x is greater\");");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{
                "INTTK int", "IDENFR x", "ASSIGN =", "INTCON 10", "SEMICN ;",
                "IFTK if", "LPARENT (", "IDENFR x", "GRE >", "INTCON 5", "RPARENT )",
                "PRINTFTK printf", "LPARENT (", "STRCON \"x is greater\"", "RPARENT )", "SEMICN ;"
        };
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testInvalidCharacters() {
        Charstream s = Charstream.fromString("@ # $");
        Lexer lexer = new Lexer(s);
        lexer.engine();
        String[] expected = new String[]{
                "UNDEF @", "UNDEF #", "UNDEF $"
        };
        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRobustLexer() {
        // 模拟复杂无规律的字符流
        Charstream s = Charstream.fromString("int123@!!/*comment*/var##=%%=23>=<><===\'a\'\"str\";\n" +
                "\t!=!/*multi-line\ncomment\n*/\n" +
                "float _var_1 = 4.56e+10; // comment\n" +
                "0xAF + 0b1101 - 'c' \n" +
                "/* nested /*comment*/end\n" +
                "if(!a&&b||c==0){return;}\n" +
                "\"string with special chars: \\n\"\n" +
                "while(x>=10&&y<=100||z==\'@\'||true){x++;}\n" +
                "//////==!!<>/*morecomments*/-->--");

        Lexer lexer = new Lexer(s);
        lexer.engine();

        // 期望的Token列表，可能包含非法Token，用于测试词法分析器的容错能力
        String[] expected = new String[]{
                "IDENFR int123", "UNDEF @", "NOT !", "NOT !",
                "IDENFR var", "UNDEF #", "UNDEF #",
                "ASSIGN =", "MOD %", "MOD %", "ASSIGN =", "INTCON 23", "GEQ >=",
                "LSS <", "GRE >", "LEQ <=", "EQL ==", "CHRCON 'a'",
                "STRCON \"str\"", "SEMICN ;", "NEQ !=",
                "NOT !", "IDENFR float", "IDENFR _var_1",
                "ASSIGN =","INTCON 4", "UNDEF .", "INTCON 56","IDENFR e",  "PLUS +", "INTCON 10", "SEMICN ;", "INTCON 0", "IDENFR xAF",
                "PLUS +", "INTCON 0", "IDENFR b1101", "MINU -", "CHRCON 'c'",
                "IDENFR end", "IFTK if", "LPARENT (", "NOT !", "IDENFR a", "AND &&",
                "IDENFR b", "OR ||", "IDENFR c", "EQL ==", "INTCON 0",
                "RPARENT )", "LBRACE {", "RETURNTK return", "SEMICN ;", "RBRACE }",
                "STRCON \"string with special chars: \\n\"",
                "IDENFR while", "LPARENT (", "IDENFR x", "GEQ >=", "INTCON 10",
                "AND &&", "IDENFR y", "LEQ <=", "INTCON 100", "OR ||", "IDENFR z",
                "EQL ==", "CHRCON '@'", "OR ||", "IDENFR true", "RPARENT )",
                "LBRACE {", "IDENFR x", "PLUS +", "PLUS +", "SEMICN ;", "RBRACE }",
        };

        String[] actual = lexer.getTokens().stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }
}
