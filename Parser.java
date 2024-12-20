import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Main parsing method
    public ProgramNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(parseStatement());
        }
        // Create ProgramNode with statements and line number
        ProgramNode program = new ProgramNode(peek().getLineNumber());
        // Add all statements to the program node
        for (ASTNode statement : statements) {
            program.addStatement(statement);
        }
        return program;
    }

    // Statement parsing methods
    private ASTNode parseStatement() {
        if (match(TokenType.INT, TokenType.FLOAT, TokenType.CHAR)) {
            return parseVariableDeclaration();
        }
        if (match(TokenType.IF)) return parseIfStatement();
        if (match(TokenType.WHILE)) return parseWhileStatement();
        if (match(TokenType.FOR)) return parseForStatement();
        if (match(TokenType.FUN)) return parseFunctionDeclaration();
        if (match(TokenType.PRINT)) return parsePrintStatement();
        if (match(TokenType.INPUT)) return parseInputStatement();
        if (match(TokenType.LEFT_BRACE)) return parseBlock();
        if (match(TokenType.RETURN)) return parseReturnStatement();
        
        return parseExpressionStatement();
    }

    private ASTNode parseExpressionStatement() {
        ASTNode expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return expr;
    }
    
    private VariableDeclarationNode parseVariableDeclaration() {
        Token typeToken = previous(); // Get the type token (INT, FLOAT, CHAR)
        String type = typeToken.getValue();
        
        Token nameToken = consume(TokenType.IDENTIFIER, "Expect variable name.");
        String name = nameToken.getValue();
        
        ASTNode initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new VariableDeclarationNode(nameToken.getLineNumber(), type, name, initializer);
    }

    private IfNode parseIfStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        ASTNode condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

        ASTNode thenBranch = parseStatement();
        ASTNode elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = parseStatement();
        }

        return new IfNode(peek().getLineNumber(), condition, thenBranch, elseBranch);
    }

    private WhileNode parseWhileStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        ASTNode condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
        ASTNode body = parseStatement();

        return new WhileNode(peek().getLineNumber(), condition, body);
    }

    private ForNode parseForStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");
        
        ASTNode initializer = null;
        if (!match(TokenType.SEMICOLON)) {
            if (match(TokenType.VAR)) {
                initializer = parseVariableDeclaration();
            } else {
                initializer = parseExpressionStatement();
            }
        }
        
        ASTNode condition = null;
        if (!match(TokenType.SEMICOLON)) {
            condition = parseExpression();
            consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");
        }
        
        ASTNode increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = parseExpression();
        }
        
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");
        ASTNode body = parseStatement();
        
        return new ForNode(peek().getLineNumber(), initializer, condition, increment, body);
    }

    private FunctionNode parseFunctionDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect function name.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");
        
        List<String> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name.").getValue());
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before function body.");
        ASTNode body = parseBlock();
        
        return new FunctionNode(name.getLineNumber(), name.getValue(), parameters, body);
    }

    private PrintNode parsePrintStatement() {
        ASTNode value = parseExpression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new PrintNode(peek().getLineNumber(), value);
    }

    private InputNode parseInputStatement() {
        String prompt = "";
        if (match(TokenType.STRING_LITERAL)) {
            prompt = previous().getValue();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after input statement.");
        return new InputNode(peek().getLineNumber(), prompt);
    }

    private ASTNode parseBlock() {
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return new BlockNode(peek().getLineNumber(), statements);
    }

    private ASTNode parseReturnStatement() {
        Token keyword = previous();
        ASTNode value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new ReturnNode(keyword.getLineNumber(), value);
    }

    // Expression parsing methods with precedence climbing
    private ASTNode parseExpression() {
        return parseAssignment();
    }

    private ASTNode parseAssignment() {
        ASTNode expr = parseOr();

        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            ASTNode value = parseAssignment();

            if (expr instanceof VariableNode) {
                String name = ((VariableNode) expr).getName();
                return new AssignmentNode(equals.getLineNumber(), name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private ASTNode parseOr() {
        ASTNode expr = parseAnd();

        while (match(TokenType.OR)) {
            Token operator = previous();
            ASTNode right = parseAnd();
            expr = new BinaryOperationNode(operator.getLineNumber(), expr, operator, right);
        }

        return expr;
    }

    private ASTNode parseAnd() {
        ASTNode expr = parseEquality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            ASTNode right = parseEquality();
            expr = new BinaryOperationNode(operator.getLineNumber(), expr, operator, right);
        }

        return expr;
    }

    private ASTNode parseEquality() {
        ASTNode expr = parseComparison();

        while (match(TokenType.EQUAL, TokenType.NOT_EQUAL)) {
            Token operator = previous();
            ASTNode right = parseComparison();
            expr = new BinaryOperationNode(operator.getLineNumber(), expr, operator, right);
        }

        return expr;
    }

    private ASTNode parseComparison() {
        ASTNode expr = parseTerm();

        while (match(TokenType.LESS, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            ASTNode right = parseTerm();
            expr = new BinaryOperationNode(operator.getLineNumber(), expr, operator, right);
        }

        return expr;
    }

    private ASTNode parseTerm() {
        ASTNode expr = parseFactor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = parseFactor();
            expr = new BinaryOperationNode(operator.getLineNumber(), expr, operator, right);
        }

        return expr;
    }

    private ASTNode parseFactor() {
        ASTNode expr = parseUnary();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            Token operator = previous();
            ASTNode right = parseUnary();
            expr = new BinaryOperationNode(operator.getLineNumber(), expr, operator, right);
        }

        return expr;
    }

    private ASTNode parseUnary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = parseUnary();
            return new UnaryOperationNode(operator.getLineNumber(), operator, right);
        }

        return parsePrimary();
    }

    private ASTNode parsePrimary() throws ParserException {
        if (match(TokenType.NUMBER)) {
            Token numberToken = previous();
            Object value;
            String lexeme = numberToken.getValue();
    
            // Check if the number contains a decimal point to determine its type
            if (lexeme.contains(".")) {
                try {
                    value = Double.parseDouble(lexeme);
                } catch (NumberFormatException e) {
                    throw new ParserException("Invalid float literal: " + lexeme, numberToken);
                }
            } else {
                try {
                    value = Integer.parseInt(lexeme);
                } catch (NumberFormatException e) {
                    throw new ParserException("Invalid integer literal: " + lexeme, numberToken);
                }
            }
    
            return new LiteralNode(numberToken.getLineNumber(), value);
        }
    
        if (match(TokenType.STRING_LITERAL)) {
            Token stringToken = previous();
            return new LiteralNode(stringToken.getLineNumber(), stringToken.getValue());
        }
    
        if (match(TokenType.IDENTIFIER)) {
            Token identifierToken = previous();
            return new VariableNode(identifierToken.getLineNumber(), identifierToken.getValue());
        }
    
        if (match(TokenType.LEFT_PAREN)) {
            ASTNode expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return expr;
        }
    
        throw error(peek(), "Expect expression.");
    }

    // Helper methods
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParserException error(Token token, String message) {
        return new ParserException(message, token);
    }

    private ASTNode parseIdentifier() {
        Token name = previous();
        if (match(TokenType.LEFT_PAREN)) {
            List<ASTNode> arguments = new ArrayList<>();
            if (!check(TokenType.RIGHT_PAREN)) {
                do {
                    arguments.add(parseExpression());
                } while (match(TokenType.COMMA));
            }
            consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
            return new FunctionCallNode(name.getLineNumber(), name.getValue(), arguments);
        }
        return new VariableNode(name.getLineNumber(), name.getValue());
    }
}