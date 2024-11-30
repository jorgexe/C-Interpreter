public interface ASTVisitor<T> {
    T visit(ProgramNode node);
    T visit(VariableDeclarationNode node);
    T visit(AssignmentNode node);
    T visit(BinaryOperationNode node);
    T visit(IfNode node);
    T visit(WhileNode node);
    T visit(FunctionNode node);
    T visit(FunctionCallNode node);
    T visit(PrintNode node);
    T visit(InputNode node);
    T visit(ForNode node);
    T visit(ReturnNode node);
    T visit(BlockNode node);
    T visit(VariableNode node);
}
