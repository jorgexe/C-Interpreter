import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, String> variables;
    private SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.variables = new HashMap<>();
        this.parent = parent;
    }

    /**
     * Defines a new variable in the current scope.
     *
     * @param name The name of the variable.
     * @param type The type of the variable (e.g., "int", "float", "string").
     */
    public void define(String name, String type) {
        variables.put(name, type);
    }

    /**
     * Resolves a variable by name, searching the current scope and parent scopes.
     *
     * @param name The name of the variable.
     * @return The type of the variable if found; otherwise, null.
     */
    public String resolve(String name) {
        String type = variables.get(name);
        if (type != null) {
            return type;
        } else if (parent != null) {
            return parent.resolve(name);
        } else {
            return null; // Variable not found in any scope
        }
    }

    /**
     * Resolves a variable by name in the current scope only.
     *
     * @param name The name of the variable.
     * @return The type of the variable if found in the current scope; otherwise, null.
     */
    public String resolveCurrentScope(String name) {
        return variables.get(name);
    }

    /**
     * Gets the parent symbol table (enclosing scope).
     *
     * @return The parent symbol table.
     */
    public SymbolTable getParent() {
        return parent;
    }

    /**
     * Returns a string representation of the symbol table for debugging purposes.
     *
     * @return A string representing the symbol table.
     */
    @Override
    public String toString() {
        return "SymbolTable{" +
                "variables=" + variables +
                ", parent=" + (parent != null ? "SymbolTable@" + parent.hashCode() : "null") +
                '}';
    }
}