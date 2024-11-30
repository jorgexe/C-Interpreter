public class ReturnNode extends ASTNode {
    private ASTNode value;

    public ReturnNode(int lineNumber, ASTNode value) {
        super(lineNumber);
        this.value = value;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}