package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre volanie funkcie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class FunctionCall extends Node {
    /** Atribút name obashuje vrchol pre názov volanej funkcie. **/
    Node name;

    /** Atribút arguments obsahuje vrchol pre zoznam argumentov volanej funkcie. **/
    Node arguments;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Assignment} a inicilizuje jej atribúty.
     *
     * <p> V konštruktori sa zároveň kontrolujú názvy funkcií. V prípade, ak ide o funkciu scanf, je potrebné pridať
     * do symbolickej tabuľky deklarácie premenných.
     *
     * @param name vrchol pre názov volanej funkcie
     *
     * @param args vrchol pre zoznam argumentov
     *
     * @param line riadok využitia
     *
     * @param symbolTable symbolická tabuľka
     */
    public FunctionCall(Node name, Node args, int line, SymbolTable symbolTable) {
        this.name = name;
        this.arguments = args;
        setLine(line);

        checkFunction(symbolTable);
    }

    /**
     * Metóda na pridanie deklarácie premenných do symbolickej tabuľky, v prípade ak ide o funkciu "scanf".
     *
     * @param symbolTable symbolická tabuľka
     */
    private void checkFunction(SymbolTable symbolTable) {
        if (name instanceof Identifier) {
            String funcName = ((Identifier) name).getName();
            if ((funcName.equals("scanf") || funcName.equals("scanf_s")) && arguments instanceof ExpressionList) {
                for(Node node : ((ExpressionList) arguments).getExpressions()) {
                    if (node instanceof UnaryOperator && ((UnaryOperator) node).getOperator().equals("&")) {
                        SymbolTableFiller.resolveInitialization(((UnaryOperator) node).getExpression(), symbolTable, node.getLine());
                    } else if (node instanceof Identifier) {
                        SymbolTableFiller.resolveInitialization(node, symbolTable, node.getLine());
                    }
                }
            }
        }
    }

    /**
     * Metóda na zistenie vrcholu pre názov funckie.
     *
     * @return vrchol pre názov funkcie.
     */
    @Override
    public Node getNameNode() {
        return name;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "FunctionCall: ");
        if (name != null) name.traverse(indent + "    ");
        if (arguments != null) arguments.traverse(indent + "    ");
    }

}
