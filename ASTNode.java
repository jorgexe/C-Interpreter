public abstract class ASTNode {
    // Base class for all AST nodes
    private int lineNumber;

    public ASTNode(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public abstract <T> T accept(ASTVisitor<T> visitor);
}