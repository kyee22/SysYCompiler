// Copyright (C) 2024 Yixuan Kuang <kyee22@buaa.edu.cn>

lexer grammar SysYLexer ;

MAIN    : 'main';

CONST   : 'const';

INT     : 'int';

CHAR    : 'char';

VOID    : 'void';

IF      : 'if';

ELSE    : 'else';

FOR   : 'for';

BREAK   : 'break';

CONTINUE: 'continue';

RETURN  : 'return';

GETINT  : 'getint';

GETCHAR : 'getchar';

PRINTF  : 'printf';

PLUS    : '+';

MINUS   : '-';

MUL     : '*';

DIV     : '/';

MOD     : '%';

ASSIGN  : '=';

EQ      : '==';

NEQ     : '!=';

LT      : '<';

GT      : '>';

LE      : '<=';

GE      : '>=';

NOT     : '!';

AND     : '&&';

OR      : '||';

L_PAREN : '(';

R_PAREN : ')';

L_BRACE : '{';

R_BRACE : '}';

L_BRACKT: '[';

R_BRACKT: ']';

COMMA   : ',';

SEMICOLON: ';';


IDENT : /* 以下划线或字母开头，仅包含下划线、英文字母大小写、阿拉伯数字 */
        ('_' | [a-zA-Z]) ('_' | [a-zA-Z0-9])*
   ;

INTEGER_CONST : '0' | [1-9] [0-9]*
;

// 定义 CHARACTER_CONST，包含 ASCII 32-126 范围的字符，包括空格，并对 \n, \", \', \\ 的处理
CHARACTER_CONST :
  '\''      ( '\\n'         // 换行符
            | '\\t'         // 制表符
            | '\\\\'        // 反斜杠
            | '"'           // 双引号
            | '\\\''        // 单引号
            | '\\a'
            | '\\b'
            | '\\v'
            | '\\f'
            | '\\0'
            | [\u0020-\u007E] // 包含ASCII 32-126范围内的字符
            )
  '\''
            ;


// 定义字符串常量
STRING_CONST :
  '"'      ( '\\n'          // 换行符
            | '\\t'         // 制表符
            | '\\\\'        // 反斜杠
            | '\\"'           // 双引号
            | '\\\''        // 单引号
            | '\\a'
            | '\\b'
            | '\\v'
            | '\\f'
            | '\\0'
            | [\u0020-\u007E] // 包含ASCII 32-126范围内的字符
            )*
  '"'
            ;

// 跳过空白符和注释
WS  : [ \n\t\r]+ -> skip;  // 跳过空白符
LINE_COMMENT: '//' .*? '\n' -> skip;
MULTILINE_COMMENT : '/*' .*? '*/' -> skip;
