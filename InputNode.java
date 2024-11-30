public class InputNode extends ASTNode {
    private String prompt;

    public InputNode(int lineNumber, String prompt) {
        super(lineNumber);
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}