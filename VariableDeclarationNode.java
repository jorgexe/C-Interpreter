public class VariableDeclarationNode extends ASTNode {
    private String variableName;
    private ASTNode initializer;

    public VariableDeclarationNode(int lineNumber, String variableName, ASTNode initializer) {
        super(lineNumber);
        this.variableName = variableName;
        this.initializer = initializer;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getInitializer() {
        return initializer;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}