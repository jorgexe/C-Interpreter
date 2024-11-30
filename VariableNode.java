public class VariableNode extends ASTNode {
    private String name;

    public VariableNode(int lineNumber, String name) {
        super(lineNumber);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}