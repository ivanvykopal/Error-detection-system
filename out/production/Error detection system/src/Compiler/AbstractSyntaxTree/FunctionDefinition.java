package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre definíciu funkcie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class FunctionDefinition extends Node {
    /** Atribút declaration predstavuje vrchol pre deklaráciu funckie. **/
    Node declaration;

    /** Atribút parameters predstavuje zoznam parametrov. **/
    ArrayList<Node> parameters;

    /** Atribút body predstavuje telo funkcie. **/
    Node body;

    /**
     * Konštruktor, ktorý vytvára triedu {@code FunctionDefinition} a inicilizuje jej atribúty.
     *
     * @param decl vrchol pre deklaráciu funkcie
     *
     * @param params zoznam parametrov
     *
     * @param body telo funckie
     *
     * @param line riadok využitia
     */
    public FunctionDefinition(Node decl, ArrayList<Node> params, Node body, int line) {
        this.declaration = decl;
        this.parameters = params;
        this.body = body;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "FunctionDefinition: ");
        if (declaration != null) declaration.traverse(indent + "    ");
        if (parameters != null) {
            for (Node param : parameters) {
                param.traverse(indent + "    ");
            }
        }
        if (body != null) body.traverse(indent + "    ");
    }

}
