public class ParserException extends InterpreterException {
    private final Token token;

    public ParserException(String message, Token token) {
        super(message, token.getLineNumber());
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}