
//TODO: alterar as palavras dos tokens para a linguagem do compilador kécia
package lexical.example;

public enum TipoToken {
    // SPECIALS
    UNEXPECTED_EOF,
    INVALID_TOKEN,
    END_OF_FILE,
    APP, //app
    INIT, //init
    RETURN, //return

    // SYMBOLS
    NULLABLE,     //?
    SEMICOLON,     // ;
    ASSIGN,        // :=
    PARENTESES_L, //(
    PARENTESES_R, //)
    SQUARE_BRACKETS_L, //[
    SQUARE_BRACKETS_R, //]
    COMMA, //,
    CURLY_BRACKETS_L, //{
    CURLY_BRACKETS_R, //}
    TWO_POINTS, //:
    THREE_POINT,//...

    // OPERATORS
    ADD,           // +
    SUB,           // -
    MUL,           // *
    DIV,           // /
    MOD,           // %
    EQUAL,         // =
    NOT_EQUAL,     // !=
    LOWER,         // <
    LOWER_EQUAL,   // <=
    GREATER,       // >
    GREATER_EQUAL, // >=
    AND, //&&
    OR, //||
    ELSE, // else
    IN, // in
    DENIAL, //!
    DECREMENT,//--
    INCREMENT,//++
    IF, //if
    // KEYWORDS
    TERNARY, //?
    VAR,      //var,
    VARN,     //var?
    FINAL,   //final
    ASSERT, //assert
    PRINT,  //print
    WHILE, //while
    DO,    //do
    FOR,   //for
    READ, //read
    RANDOM, //random
    LENGTH, //length
    KEYS, //keys
    VALUES, //values
    TOBOOL, //tobool
    TOINT, //toint
    TOSTR, //torst
    // OTHERS
    FALSE,
    TRUE,
    NULL,
    NAME,          // identifier
    NUMBER,        // integer
    FLOAT,        // float
    TEXT,

    INTEGER,

    REAL,// string

    THEN,

    END,

    REPEAT,

    UNTIL,

    WRITE,


};
