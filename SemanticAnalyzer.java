public class SemanticAnalyzer implements ASTVisitor<Void> {
    private SymbolTable symbolTable;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable(null); // Global scope
    }

    @Override
    public Void visit(ProgramNode node) {
        // Start semantic analysis from the global scope
        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        String name = node.getVariableName();
        String type = node.getType();

        // Check if the variable is already declared in the current scope
        if (symbolTable.resolveCurrentScope(name) != null) {
            throw new SemanticException("Variable '" + name + "' is already declared.", node.getLineNumber());
        }

        // Analyze the initializer expression if it exists
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
            String initializerType = node.getInitializer().getType();

            // Check type compatibility between variable type and initializer type
            if (!isTypeCompatible(type, initializerType)) {
                throw new SemanticException("Type mismatch in initialization of variable '" + name + "'. Expected type '" + type + "', but got '" + initializerType + "'.", node.getLineNumber());
            }
        }

        // Add the variable to the symbol table
        symbolTable.define(name, type);
        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        String name = node.getVariableName();
        String variableType = symbolTable.resolve(name);

        if (variableType == null) {
            throw new SemanticException("Variable '" + name + "' is not declared.", node.getLineNumber());
        }

        // Analyze the assigned value
        node.getValue().accept(this);
        String valueType = node.getValue().getType();

        // Check type compatibility
        if (!isTypeCompatible(variableType, valueType)) {
            throw new SemanticException("Type mismatch in assignment to variable '" + name + "'. Variable type is '" + variableType + "', but assigned value is of type '" + valueType + "'.", node.getLineNumber());
        }

        return null;
    }

    @Override
    public Void visit(VariableNode node) {
        String name = node.getName();
        String type = symbolTable.resolve(name);

        if (type == null) {
            throw new SemanticException("Variable '" + name + "' is not declared.", node.getLineNumber());
        }

        // Set the type of the variable node
        node.setType(type);
        return null;
    }

    @Override
    public Void visit(BinaryOperationNode node) {
        // Analyze left and right operands
        node.getLeft().accept(this);
        node.getRight().accept(this);

        String leftType = node.getLeft().getType();
        String rightType = node.getRight().getType();

        // Check type compatibility
        //if (!isTypeCompatible(leftType, rightType)) {
        //    throw new SemanticException("Type mismatch in binary operation. Left operand is of type '" + leftType + "', right operand is of type '" + rightType + "'.", node.getLineNumber());
        //}
        // Determine result type
        //node.setType(getResultType(leftType, rightType));
        switch (node.getOperator().getType()) {
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
                // Comparison operators should work with numeric types
                if (!isTypeCompatible(leftType, rightType)) {
                    throw new SemanticException(
                        "Invalid operands for comparison. Got '" + leftType + "' and '" + rightType + "'.",
                        node.getLineNumber()
                    );
                }
                // Comparison operations always return boolean
                node.setType("bool");
                break;
    
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
                if (!isTypeCompatible(leftType, rightType)) {
                    throw new SemanticException(
                        "Type mismatch in arithmetic operation. Left operand is '" + 
                        leftType + "', right operand is '" + rightType + "'.",
                        node.getLineNumber()
                    );
                }
                node.setType(getResultType(leftType, rightType));
                break;
    
            default:
                throw new SemanticException(
                    "Unknown operator '" + node.getOperator().getValue() + "' in binary operation.",
                    node.getLineNumber()
                );
        }

        return null;
    }

    @Override
    public Void visit(UnaryOperationNode node) {
        // Analyze the operand
        node.getOperand().accept(this);
        String operandType = node.getOperand().getType();

        // For simplicity, assume unary operations are only for numeric types
        if (!operandType.equals("int") && !operandType.equals("float")) {
            throw new SemanticException("Unary operator applied to non-numeric type '" + operandType + "'.", node.getLineNumber());
        }

        // Set the type of the unary operation node
        node.setType(operandType);
        return null;
    }

    @Override
    public Void visit(LiteralNode node) {
        Object value = node.getValue();
        String type;

        if (value instanceof Integer) {
            type = "int";
        } else if (value instanceof Double) {
            type = "float";
        } else if (value instanceof String) {
            type = "string";
        } else if (value instanceof Character) {
            type = "char";
        } else if (value instanceof Boolean) {
            type = "bool";
        } else {
            type = "null";
        }

        node.setType(type);
        return null;
    }

    @Override
    public Void visit(PrintNode node) {
        node.getExpression().accept(this);
        // No type checking necessary for print statements
        return null;
    }

    @Override
    public Void visit(InputNode node) {
        // Assuming input returns a string
        node.setType("string");
        return null;
    }

    @Override
    public Void visit(BlockNode node) {
        // Create a new scope
        symbolTable = new SymbolTable(symbolTable);

        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);
        }

        // Exit the scope
        symbolTable = symbolTable.getParent();
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        // Analyze the condition
        node.getCondition().accept(this);
        String conditionType = node.getCondition().getType();
        
        if (!conditionType.equals("bool")) {
            throw new SemanticException("Condition in 'if' statement must be of type 'bool', but got '" + conditionType + "'.", node.getLineNumber());
        }
        
        // Analyze the 'then' branch
        node.getThenBranch().accept(this);

        // Analyze the 'else' branch if it exists
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        // Analyze the condition
        node.getCondition().accept(this);
        String conditionType = node.getCondition().getType();
        
        if (!conditionType.equals("bool")) {
            throw new SemanticException("Condition in 'while' loop must be of type 'bool', but got '" + conditionType + "'.", node.getLineNumber());
        }
        
        // Analyze the loop body
        node.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(ForNode node) {
        // Create a new scope
        symbolTable = new SymbolTable(symbolTable);

        // Analyze the initializer
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }

        // Analyze the condition
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
            String conditionType = node.getCondition().getType();

            if (!conditionType.equals("bool")) {
                throw new SemanticException("Condition in 'for' loop must be of type 'bool', but got '" + conditionType + "'.", node.getLineNumber());
            }
        }

        // Analyze the increment
        if (node.getIncrement() != null) {
            node.getIncrement().accept(this);
        }

        // Analyze the loop body
        node.getBody().accept(this);

        // Exit the scope
        symbolTable = symbolTable.getParent();
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        // Function handling can be implemented as needed
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        // Function call handling can be implemented as needed
        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        if (node.getValue() != null) {
            node.getValue().accept(this);
            // Return type checking can be implemented if functions are used
        }
        return null;
    }

    // Helper method to check type compatibility
    private boolean isTypeCompatible(String expected, String actual) {
        if (expected.equals(actual)) {
            return true;
        }

        // Allow implicit conversion from int to float
        if (expected.equals("float") && actual.equals("int")) {
            return true;
        }

        //Allow int to float
        if (expected.equals("int") && actual.equals("float")) {
            return true;
        }

        // Add more type compatibility rules as needed
        return false;
    }

    // Helper method to determine result type of binary operations
    private String getResultType(String leftType, String rightType) {
        if (leftType.equals("float") || rightType.equals("float")) {
            return "float";
        }
        return leftType;
    }
}