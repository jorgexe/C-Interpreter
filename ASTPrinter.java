import java.util.List;
import java.util.stream.Collectors;

public class ASTPrinter implements ASTVisitor<String> {
    private int indent = 0;
    
    public String print(ASTNode node) {
        return node.accept(this);
    }

    private String getIndentation() {
        return "  ".repeat(indent);
    }

    @Override
    public String visit(ProgramNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Program:\n");
        indent++;
        for (ASTNode statement : node.getStatements()) {
            sb.append(getIndentation()).append(statement.accept(this)).append("\n");
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visit(VariableDeclarationNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("VarDecl: ").append(node.getType()).append(" ").append(node.getVariableName());
        if (node.getInitializer() != null) {
            sb.append(" = ").append(node.getInitializer().accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visit(AssignmentNode node) {
        return "Assign: " + node.getVariableName() + " = " + node.getValue().accept(this);
    }

    @Override
    public String visit(BinaryOperationNode node) {
        return "(" + node.getLeft().accept(this) + " " + 
               node.getOperator().getValue() + " " + 
               node.getRight().accept(this) + ")";
    }

    @Override
    public String visit(IfNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("If:\n");
        indent++;
        sb.append(getIndentation()).append("Condition: ").append(node.getCondition().accept(this)).append("\n");
        sb.append(getIndentation()).append("Then: ").append(node.getThenBranch().accept(this));
        if (node.getElseBranch() != null) {
            sb.append("\n").append(getIndentation()).append("Else: ").append(node.getElseBranch().accept(this));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visit(WhileNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("While:\n");
        indent++;
        sb.append(getIndentation()).append("Condition: ").append(node.getCondition().accept(this)).append("\n");
        sb.append(getIndentation()).append("Body: ").append(node.getBody().accept(this));
        indent--;
        return sb.toString();
    }

    @Override
    public String visit(FunctionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Function: ").append(node.getFunctionName()).append("\n");
        indent++;
        sb.append(getIndentation()).append("Parameters: ").append(String.join(", ", node.getParameters())).append("\n");
        sb.append(getIndentation()).append("Body: ").append(node.getBody().accept(this));
        indent--;
        return sb.toString();
    }

    @Override
    public String visit(FunctionCallNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Call: ").append(node.getFunctionName()).append("(");
        List<String> args = node.getArguments().stream()
            .map(arg -> arg.accept(this))
            .collect(Collectors.toList());
        sb.append(String.join(", ", args)).append(")");
        return sb.toString();
    }

    @Override
    public String visit(PrintNode node) {
        return "Print: " + node.getExpression().accept(this);
    }

    @Override
    public String visit(InputNode node) {
        return "Input: " + (node.getPrompt().isEmpty() ? "" : "\"" + node.getPrompt() + "\"");
    }

    @Override
    public String visit(BlockNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Block:\n");
        indent++;
        for (ASTNode statement : node.getStatements()) {
            sb.append(getIndentation()).append(statement.accept(this)).append("\n");
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visit(ReturnNode node) {
        return "Return: " + (node.getValue() != null ? node.getValue().accept(this) : "void");
    }

    @Override
    public String visit(UnaryOperationNode node) {
        return node.getOperator().getValue() + node.getOperand().accept(this);
    }

    @Override
    public String visit(LiteralNode node) {
        Object value = node.getValue();
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return String.valueOf(value);
    }

    @Override
    public String visit(VariableNode node) {
        return node.getName();
    }

    @Override
    public String visit(ForNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("For:\n");
        indent++;
        sb.append(getIndentation()).append("Init: ");
        sb.append(node.getInitializer() != null ? node.getInitializer().accept(this) : "none").append("\n");
        sb.append(getIndentation()).append("Condition: ");
        sb.append(node.getCondition() != null ? node.getCondition().accept(this) : "none").append("\n");
        sb.append(getIndentation()).append("Increment: ");
        sb.append(node.getIncrement() != null ? node.getIncrement().accept(this) : "none").append("\n");
        sb.append(getIndentation()).append("Body: ").append(node.getBody().accept(this));
        indent--;
        return sb.toString();
    }
}