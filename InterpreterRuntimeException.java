public class InterpreterRuntimeException extends InterpreterException {
    private final Object value;

    public InterpreterRuntimeException(String message, int lineNumber) {
        super(message, lineNumber);
        this.value = null;
    }

    public InterpreterRuntimeException(String message, int lineNumber, Object value) {
        super(message, lineNumber);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}