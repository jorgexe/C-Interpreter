public enum TokenType {
    // Keywords
    INT, FLOAT, CHAR, IF, ELSE, WHILE, RETURN, PRINT, INPUT, 
    
    // Literals
    IDENTIFIER, NUMBER, CHAR_LITERAL, STRING_LITERAL,
    
    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, 
    ASSIGN, // "="
    EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, // "==", "!=", "<", ">", "<=", ">="
    AND, OR, NOT, // "&&", "||", "!"

    // Punctuation
    SEMICOLON, COMMA, LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,

    // Special
    EOF // End of File
}
