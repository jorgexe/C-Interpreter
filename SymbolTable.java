import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, String> variables;
    private SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.variables = new HashMap<>();
        this.parent = parent;
    }


    public void define(String name, String type) {
        variables.put(name, type);
    }

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

    public String resolveCurrentScope(String name) {
        return variables.get(name);
    }

    public SymbolTable getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "variables=" + variables +
                ", parent=" + (parent != null ? "SymbolTable@" + parent.hashCode() : "null") +
                '}';
    }
}