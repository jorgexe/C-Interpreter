import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;

public class CInterpreter {
    public static void main(String[] args) {
        // Get the source code
        String filePath = "/Users/jorgesandoval/Library/Mobile Documents/com~apple~CloudDocs/Main/school/UABC/semestres/02 etapa disciplinaria/semestre_6/materias/T/interprete/C-Interpreter/CSourceCode.txt";
        String source = "";
        try {
            source = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Print Lexical Analysis
        System.out.println("\n *** Lexical Analysis *** \n");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // Syntax Analysis
        ProgramNode ast = null;
        try {
            Parser parser = new Parser(tokens);
            ast = parser.parse();
            
            System.out.println("\n *** Syntax Analysis *** \n");
            ASTPrinter astPrinter = new ASTPrinter();
            System.out.println(astPrinter.print(ast));
        } catch (ParserException e) {
            System.err.println("Syntax Error: Line " + e.getLineNumber() + ": " + e.getMessage());
            // Print the problematic line for context
            String[] lines = source.split("\n");
            // e.getLineNumber() is not available.
        }

        // Semantic Analysis
        try {
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.visit(ast);

            System.out.println("\n *** Semantic Analysis *** \n");
            System.out.println("Semantic analysis completed successfully.");
        } catch (SemanticException e) {
            System.err.println("Semantic Error: Line " + e.getLineNumber() + ": " + e.getMessage());
            // Optionally, print the problematic line for context
            String[] lines = source.split("\n");
            if (e.getLineNumber() <= lines.length) {
                System.err.println("Near: " + lines[e.getLineNumber() - 1]);
            }
            return; // Exit if semantic analysis failed
        } 

        // Interpretation
        System.out.println("\n *** Interpretation *** \n");
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.visit(ast);

            System.out.println("\n *** Execution Finished *** \n");
        } catch (Interpreter.InterpreterRuntimeException e) {
            System.err.println("Runtime Error: " + e.getMessage());
            // Optionally, print the problematic line for context
            String[] lines = source.split("\n");
            if (e.getLineNumber() <= lines.length) {
                System.err.println("Near: " + lines[e.getLineNumber() - 1]);
            }
        }

    }
}