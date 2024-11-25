public class Token {
    private final TokenType type;  // The type of the token
    private final String value;   // The raw text value of the token
    private final int lineNumber; // The line number where the token was found

    public Token(TokenType type, String value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return String.format("Token{type=%s, value='%s', line=%d}", type, value, lineNumber);
    }
}
