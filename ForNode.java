public class ForNode extends ASTNode {
    private ASTNode initializer;
    private ASTNode condition;
    private ASTNode increment;
    private ASTNode body;

    public ForNode(int lineNumber, ASTNode initializer, ASTNode condition, ASTNode increment, ASTNode body) {
        super(lineNumber);
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    public ASTNode getInitializer() {
        return initializer;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getIncrement() {
        return increment;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
