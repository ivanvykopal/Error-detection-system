package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre zoznam parametrov funkcie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class ParameterList extends Node {
    /** Atribút parameters predstavuje zoznam parametrov funkcie. **/
    ArrayList<Node> parameters;

    /**
     * Konštruktor, ktorý vytvára triedu {@code ParameterList} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných jednotlivých parametrov do symbolickej tabuľky.
     *
     * @param params zoznam parametrov
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public ParameterList(ArrayList<Node> params, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.parameters = params;
        setLine(line);

        for (Node node: parameters) {
            SymbolTableFiller.resolveUsage(node, table, errorDatabase, true, true);
        }
    }

    /**
     * Metóda pre pridanie yužitia premenných v rámci {@code Assignment}, pre zadaný riadok.
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, na ktorom sa premenné využívajú
     */
    @Override
    public void resolveUsage(SymbolTable table, int line) {
        for (Node node : parameters) {
            SymbolTableFiller.resolveUsage(node, table, line);
            node.resolveUsage(table, line);
        }
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ParameterList: ");
        if (parameters != null) {
            for (Node param : parameters) {
                param.traverse(indent + "    ");
            }
        }
    }

    /**
     * Metóda na pridanie parametra do zoznamu parametrov funkcie.
     *
     * @param param parameter funckie
     */
    public void addParameter(Node param) {
        parameters.add(param);
    }

    /**
     * Metóda na zistenie zoznamu parametrov funkcie.
     *
     * @return zoznam paramtrov funkcie.
     */
    public ArrayList<Node> getParameters() {
        return parameters;
    }

}
