public class AssignmentNode extends ASTNode {
    private String variableName;
    private ASTNode value;

    public AssignmentNode(int lineNumber, String variableName, ASTNode value) {
        super(lineNumber);
        this.variableName = variableName;
        this.value = value;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}