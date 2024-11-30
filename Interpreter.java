import java.util.HashMap;
import java.util.Map;

public class Interpreter implements ASTVisitor<Object> {
    private Environment environment;

    public Interpreter() {
        this.environment = new Environment(null); // Global environment
    }

    @Override
    public Object visit(ProgramNode node) {
        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(VariableDeclarationNode node) {
        String name = node.getVariableName();
        Object value = null;
        if (node.getInitializer() != null) {
            value = node.getInitializer().accept(this);
        }
        environment.define(name, value);
        return null;
    }

    @Override
    public Object visit(AssignmentNode node) {
        String name = node.getVariableName();
        Object value = node.getValue().accept(this);
        environment.assign(name, value);
        return null;
    }

    @Override
    public Object visit(VariableNode node) {
        return environment.get(node.getName());
    }

    @Override
    public Object visit(LiteralNode node) {
        return node.getValue();
    }

    @Override
    public Object visit(BinaryOperationNode node) {
        Object left = node.getLeft().accept(this);
        Object right = node.getRight().accept(this);
        Token operator = node.getOperator();

        switch (operator.getType()) {
            case PLUS:
                if (left instanceof Double || right instanceof Double) {
                    return toDouble(left) + toDouble(right);
                } else if (left instanceof Integer && right instanceof Integer) {
                    return (Integer) left + (Integer) right;
                } else if (left instanceof String || right instanceof String) {
                    return left.toString() + right.toString();
                }
                break;
            case MINUS:
                return toDouble(left) - toDouble(right);
            case MULTIPLY:
                return toDouble(left) * toDouble(right);
            case DIVIDE:
                return toDouble(left) / toDouble(right);
            case GREATER:
                return toDouble(left) > toDouble(right);
            case GREATER_EQUAL:
                return toDouble(left) >= toDouble(right);
            case LESS:
                return toDouble(left) < toDouble(right);
            case LESS_EQUAL:
                return toDouble(left) <= toDouble(right);
            case EQUAL:
                return isEqual(left, right);
            case NOT_EQUAL:
                return !isEqual(left, right);
            default:
                throw new InterpreterRuntimeException("Unknown operator: " + operator.getValue(), node.getLineNumber());
        }
        throw new InterpreterRuntimeException("Invalid operands for operator '" + operator.getValue() + "'", node.getLineNumber());
    }

    @Override
    public Object visit(UnaryOperationNode node) {
        Object operand = node.getOperand().accept(this);
        Token operator = node.getOperator();

        switch (operator.getType()) {
            case MINUS:
                return -toDouble(operand);
            case NOT:
                return !isTruthy(operand);
            default:
                throw new InterpreterRuntimeException("Unknown unary operator: " + operator.getValue(), node.getLineNumber());
        }
    }

    @Override
    public Object visit(PrintNode node) {
        Object value = node.getExpression().accept(this);
        System.out.println(value);
        return null;
    }

    @Override
    public Object visit(InputNode node) {
        return "user_input";
    }

    @Override
    public Object visit(BlockNode node) {
        // Create a new environment (scope)
        Environment previous = environment;
        environment = new Environment(environment);

        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);
        }

        // Restore the previous environment
        environment = previous;
        return null;
    }

    @Override
    public Object visit(IfNode node) {
        Object condition = node.getCondition().accept(this);

        if (isTruthy(condition)) {
            node.getThenBranch().accept(this);
        } else if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(WhileNode node) {
        while (true) {
            Object condition = node.getCondition().accept(this);
            if (!isTruthy(condition)) {
                break;
            }
            node.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ForNode node) {
        // For loops are transformed into while loops during parsing or here
        // Create a new environment (scope)
        Environment previous = environment;
        environment = new Environment(environment);

        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }

        while (true) {
            // Evaluate condition
            Object condition = node.getCondition() != null ? node.getCondition().accept(this) : true;
            if (!isTruthy(condition)) {
                break;
            }
            // Execute body
            node.getBody().accept(this);

            // Execute increment
            if (node.getIncrement() != null) {
                node.getIncrement().accept(this);
            }
        }

        // Restore the previous environment
        environment = previous;
        return null;
    }

    @Override
    public Object visit(FunctionNode node) {
        return null;
    }

    @Override
    public Object visit(FunctionCallNode node) {
        return null;
    }

    @Override
    public Object visit(ReturnNode node) {
        Object value = null;
        if (node.getValue() != null) {
            value = node.getValue().accept(this);
        }
        throw new ReturnException(value);
    }

    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private double toDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new InterpreterRuntimeException("Expected a number, but got '" + value + "'", 0);
        }
    }

    // Inner classes and exceptions

    public class Environment {
        private final Map<String, Object> values = new HashMap<>();
        private final Environment parent;

        public Environment(Environment parent) {
            this.parent = parent;
        }

        public void define(String name, Object value) {
            values.put(name, value);
        }

        public void assign(String name, Object value) {
            if (values.containsKey(name)) {
                values.put(name, value);
            } else if (parent != null) {
                parent.assign(name, value);
            } else {
                throw new InterpreterRuntimeException("Undefined variable '" + name + "'", 0);
            }
        }

        public Object get(String name) {
            if (values.containsKey(name)) {
                return values.get(name);
            } else if (parent != null) {
                return parent.get(name);
            } else {
                throw new InterpreterRuntimeException("Undefined variable '" + name + "'", 0);
            }
        }
    }

    public static class ReturnException extends RuntimeException {
        private final Object value;

        public ReturnException(Object value) {
            super(null, null, false, false);
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }

    public static class InterpreterRuntimeException extends RuntimeException {
        private final int lineNumber;

        public InterpreterRuntimeException(String message, int lineNumber) {
            super(message);
            this.lineNumber = lineNumber;
        }

        // Add the getter method for lineNumber
        public int getLineNumber() {
            return lineNumber;
        }

        @Override
        public String getMessage() {
            return super.getMessage() + " [Line " + lineNumber + "]";
        }
    }
}