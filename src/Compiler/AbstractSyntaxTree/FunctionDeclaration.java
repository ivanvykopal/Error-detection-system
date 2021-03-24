package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre deklaráciu funkcie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class FunctionDeclaration extends Node {
    /** Atribút arguments predstavuje vrchol pre zoznam argumentov. **/
    Node arguments;

    /** Atribút type predstavuje vrchol pre typ deklarovanej funkcie. **/
    Node type;

    /**
     * Konštruktor, ktorý vytvára triedu {@code FunctionDeclaration} a inicilizuje jej atribúty.
     *
     * @param args vrchol pre zoznam argumentov
     *
     * @param type vrchol pre typ deklarovanej funkcie
     *
     * @param line riadok využitia
     */
    public FunctionDeclaration(Node args, Node type, int line) {
        this.arguments = args;
        this.type = type;
        setLine(line);
    }

    /**
     * Metóda pre zistenie vrcholu zoznamu argumentov.
     *
     * @return vrchol pre zoznam argumentov
     */
    public Node getArguments() {
        return arguments;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "FunctionDeclaration: ");
        if (type != null) type.traverse(indent + "    ");
        if (arguments != null) arguments.traverse(indent + "    ");
    }

    /**
     * Metóda, ktorá vracia vrchol pre typ deklarovanej funkcie.
     *
     * @return vrchol pre typ deklarovanej funkcie
     */
    @Override
    public Node getType() {
        return type;
    }

    /**
     * Metóda prostredníctvom, ktorej pridávame typ deklarovanéj funkcii.
     *
     * @param type vrchol pre typ deklarovanej funkcie
     */
    @Override
    public void addType(Node type) {
        this.type = type;
    }

}
