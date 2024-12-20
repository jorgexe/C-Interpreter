import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends ASTNode {
    private List<ASTNode> statements;

    public ProgramNode(int lineNumber) {
        super(lineNumber);
        this.statements = new ArrayList<>();
    }

    public void addStatement(ASTNode statement) {
        statements.add(statement);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}