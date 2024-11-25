import java.util.*;

public class Lexer {
    private final String source;      // Source code as a string
    private int position;             // Current position in the source
    private int line;                 // Current line number

    // Keywords map for quick lookup
    private static final Map<String, TokenType> KEYWORDS = Map.of(
        "int", TokenType.INT,
        "float", TokenType.FLOAT,
        "char", TokenType.CHAR,
        "if", TokenType.IF,
        "else", TokenType.ELSE,
        "while", TokenType.WHILE,
        "print", TokenType.PRINT,
        "input", TokenType.INPUT
    );

    public Lexer(String source) {
        this.source = source;
        this.position = 0;
        this.line = 1;
    }

    // Main method to tokenize the input source
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd()) {
            char current = peek();
            if (Character.isWhitespace(current)) {
                handleWhitespace();
            } else if (current == '/' && peekNext() == '/') {
                skipSingleLineComment();
            } else if (Character.isLetter(current)) {
                tokens.add(handleIdentifierOrKeyword());
            } else if (Character.isDigit(current)) {
                tokens.add(handleNumber());
            } else if (current == '\'') {
                tokens.add(handleCharLiteral());
            } else if (current == '"') {
                tokens.add(handleStringLiteral());
            } else {
                tokens.add(handleSymbol());
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line)); // Add EOF token at the end
        return tokens;
    }

    // Handle identifiers and keywords
    private Token handleIdentifierOrKeyword() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            sb.append(advance());
        }
        String value = sb.toString();
        TokenType type = KEYWORDS.getOrDefault(value, TokenType.IDENTIFIER);
        return new Token(type, value, line);
    }

    // Handle numeric literals
    private Token handleNumber() {
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;

        while (Character.isDigit(peek()) || (peek() == '.' && !isFloat)) {
            if (peek() == '.') {
                isFloat = true;
            }
            sb.append(advance());
        }

        return new Token(TokenType.NUMBER, sb.toString(), line);
    }

    // Handle character literals
    private Token handleCharLiteral() {
        advance(); // Skip opening '
        char value = advance(); // Get the character
        if (peek() != '\'') {
            throw new RuntimeException("Unterminated character literal at line " + line);
        }
        advance(); // Skip closing '
        return new Token(TokenType.CHAR_LITERAL, String.valueOf(value), line);
    }

    // Handle string literals
    private Token handleStringLiteral() {
        advance(); // Skip opening "
        StringBuilder sb = new StringBuilder();
        while (peek() != '"' && !isAtEnd()) {
            sb.append(advance());
        }
        if (isAtEnd()) {
            throw new RuntimeException("Unterminated string literal at line " + line);
        }
        advance(); // Skip closing "
        return new Token(TokenType.STRING_LITERAL, sb.toString(), line);
    }

    // Handle symbols and operators
    private Token handleSymbol() {
        char current = advance();
        switch (current) {
            case '+': return new Token(TokenType.PLUS, "+", line);
            case '-': return new Token(TokenType.MINUS, "-", line);
            case '*': return new Token(TokenType.MULTIPLY, "*", line);
            case '/': return new Token(TokenType.DIVIDE, "/", line);
            case '%': return new Token(TokenType.MODULO, "%", line);
            case '=': return new Token(match('=') ? TokenType.EQUAL : TokenType.ASSIGN, current == '=' ? "==" : "=", line);
            case '!': return new Token(match('=') ? TokenType.NOT_EQUAL : TokenType.NOT, current == '=' ? "!=" : "!", line);
            case '<': return new Token(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS, current == '=' ? "<=" : "<", line);
            case '>': return new Token(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER, current == '=' ? ">=" : ">", line);
            case '&': return match('&') ? new Token(TokenType.AND, "&&", line) : null;
            case '|': return match('|') ? new Token(TokenType.OR, "||", line) : null;
            case ';': return new Token(TokenType.SEMICOLON, ";", line);
            case ',': return new Token(TokenType.COMMA, ",", line);
            case '(': return new Token(TokenType.LEFT_PAREN, "(", line);
            case ')': return new Token(TokenType.RIGHT_PAREN, ")", line);
            case '{': return new Token(TokenType.LEFT_BRACE, "{", line);
            case '}': return new Token(TokenType.RIGHT_BRACE, "}", line);
            default:
                throw new RuntimeException("Unexpected character '" + current + "' at line " + line);
        }
    }

    // Utility methods for handling source input
    private char advance() {
        return source.charAt(position++);
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(position);
    }

    private char peekNext() {
        return (position + 1 >= source.length()) ? '\0' : source.charAt(position + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(position) != expected) return false;
        position++;
        return true;
    }

    private boolean isAtEnd() {
        return position >= source.length();
    }

    private void handleWhitespace() {
        while (Character.isWhitespace(peek())) {
            if (peek() == '\n') line++;
            advance();
        }
    }

    private void skipSingleLineComment() {
        while (peek() != '\n' && !isAtEnd()) {
            advance();
        }
    }
}
