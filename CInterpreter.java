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
    }
}