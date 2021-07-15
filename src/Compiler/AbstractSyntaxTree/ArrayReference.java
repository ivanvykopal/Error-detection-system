package Compiler.AbstractSyntaxTree;

import Backend.InternationalizationClass;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

/**
 * Trieda predstavujúca vrchol pre prístup do poľa v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class ArrayReference extends Node {
    /** Atribút obsahujúci názov premennej. **/
    Node name;

    /** Atribút obsahujúci index prístupu do poľa. **/
    Node index;

    /**
     * Konštruktor, ktorý vytvára triedu {@code ArrayReference} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň vykonáva typová kontrola prístupu do poľa.
     * V prípade, ak sa nevyskytla typová nezhoda, kontroluje sa hodnota prístupu do poľa v rámci kontroly prístupu mimo
     * pamäť.
     *
     * <p> Následne sa pridáva využitie premenných v atribúte index do symbolickej tabuľky.
     *
     * @param name vrchol pre názov premennej
     *
     * @param index vrchol pre index prístupu do poľa
     *
     * @param line riadok volania
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public ArrayReference(Node name, Node index, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.name = name;
        this.index = index;
        setLine(line);

        if (!typeCheck(table)) {
            errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-SmA-01"), "E-SmA-01");
        } else {
            findAccessError(name, index, table, errorDatabase);
        }

        SymbolTableFiller.resolveUsage(index, table, errorDatabase, true, true);
    }

    /**
     * Metóda pre kontrolu prístupu mimo pamäť.
     *
     * <p> V rámci kontroly sa testuje prístup do poľa len v prípade, ak ide o konštantu.
     *
     * @param nodeName vrchol pre názov premennej
     *
     * @param nodeIndex vrchol pre index prístupu do poľa
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    private void findAccessError(Node nodeName, Node nodeIndex, SymbolTable table, ErrorDatabase errorDatabase) {
        int arrayIndex;
        if (nodeIndex instanceof Constant) {
            try {
                arrayIndex = Integer.parseInt(((Constant) nodeIndex).getValue());
            } catch (NumberFormatException e) {
                return;
            }
        } else {
            return;
        }

        Node id = nodeName;

        while (!(id instanceof Identifier)) {
            id = id.getNameNode();
        }

        Record record = table.lookup(((Identifier) id).getName());
        if (record != null) {
            if (record.getSize() != 0 && arrayIndex >= record.getSize()) {
                errorDatabase.addErrorMessage(nodeName.getLine(), InternationalizationClass.getErrors().getString("E-RP-06"), "E-RP-06");
            }
        }
    }

    /**
     * Metóda pre typovú kontrolu vrcholu indexu prístupu do poľa.
     *
     * @param table symbolická tabuľka
     *
     * @return true, ak nie je typová nezhoda
     *         false, ak je typová nezhoda
     */
    private boolean typeCheck(SymbolTable table) {
        short type = TypeChecker.findTypeCategory(index, table);
        type = type >= 50 ? (short) (type % 50) : type;
        return type <= Type.UNSIGNEDLONGLONGINT;

    }

    /**
     * Metóda pre pridanie yužitia premenných v rámci {@code ArrayReference}, pre zadaný riadok.
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, na ktorom sa premenné využívajú
     */
    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(index, table, line);
        index.resolveUsage(table, line);
    }

    /**
     * Metóda, ktorá vracia vrchol pre názov premennej.
     *
     * @return vrchol pre názov premennej.
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
        System.out.println(indent + "ArrayReference: ");
        if (name != null) name.traverse(indent + "    ");
        if (index != null) index.traverse(indent + "    ");
    }

}
