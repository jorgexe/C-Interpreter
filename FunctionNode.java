import java.util.List;

public class FunctionNode extends ASTNode {
    private String functionName;
    private List<String> parameters;
    private ASTNode body;

    public FunctionNode(int lineNumber, String functionName, List<String> parameters, ASTNode body) {
        super(lineNumber);
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}