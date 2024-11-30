public class BinaryOperationNode extends ASTNode {
    private ASTNode left;
    private Token operator;
    private ASTNode right;

    public BinaryOperationNode(int lineNumber, ASTNode left, Token operator, ASTNode right) {
        super(lineNumber);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ASTNode getLeft() {
        return left;
    }

    public Token getOperator() {
        return operator;
    }

    public ASTNode getRight() {
        return right;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}