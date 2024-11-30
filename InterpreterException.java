public class InterpreterException extends RuntimeException {
    private final int lineNumber;

    public InterpreterException(String message, int lineNumber) {
        super(String.format("Line %d: %s", lineNumber, message));
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}