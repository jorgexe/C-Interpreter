public class WhileNode extends ASTNode {
    private ASTNode condition;
    private ASTNode body;

    public WhileNode(int lineNumber, ASTNode condition, ASTNode body) {
        super(lineNumber);
        this.condition = condition;
        this.body = body;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}