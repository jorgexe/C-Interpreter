public class LiteralNode extends ASTNode {
    private Object value;

    public LiteralNode(int lineNumber, Object value) {
        super(lineNumber);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}