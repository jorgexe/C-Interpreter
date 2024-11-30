public class PrintNode extends ASTNode {
    private ASTNode expression;

    public PrintNode(int lineNumber, ASTNode expression) {
        super(lineNumber);
        this.expression = expression;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}