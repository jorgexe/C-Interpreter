public abstract class ASTNode {
    // Base class for all AST nodes
    private int lineNumber;
    private String type;

    public ASTNode(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    // Getter and setter for the 'type' field
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public abstract <T> T accept(ASTVisitor<T> visitor);
}