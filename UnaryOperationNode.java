public class UnaryOperationNode extends ASTNode {
    private Token operator;
    private ASTNode operand;

    public UnaryOperationNode(int lineNumber, Token operator, ASTNode operand) {
        super(lineNumber);
        this.operator = operator;
        this.operand = operand;
    }

    public Token getOperator() {
        return operator;
    }

    public ASTNode getOperand() {
        return operand;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}