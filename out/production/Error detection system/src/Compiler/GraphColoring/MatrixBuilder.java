package Compiler.GraphColoring;

import Compiler.SymbolTable.Kind;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;
import java.util.*;

/**
 * Trieda pre vytvorenie matice susednosti premenných a následné spustenie farbenia grafov.
 *
 * @author Ivan Vykopal
 */
public final class MatrixBuilder {

    /**
     * Privátny konštruktor.
     */
    private MatrixBuilder() {
    }

    /**
     * Metóda pre vytvorenie matice susednosti premenných.
     *
     * @param symbolTable symbolická tabuľka
     *
     * @param indexes zoznam premenných
     *
     * @return matica susednosti
     */
    public static byte[][] createMatrix(SymbolTable symbolTable, ArrayList<Index> indexes) {
        symbolTable.setParent(null);
        int rows = indexes.size();
        byte[][] matrix = new byte[rows][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    matrix[i][j] = 1;
                } else {
                    matrix = findEdge(matrix, i, j, indexes, symbolTable);
                }
            }
        }

        return matrix;
    }

    /**
     * Metóda, ktorá pridá pre dané miesto v matici susednosti na základe súradníc i a j 1 v prípade, ak medzi premennými
     * vznikla kolízia a 0 v prípade, ak medzi premennými nevzniká kolízia.
     *
     * <p> Kolízia môže vzniknúť vtedy ak jedna z premenných je globálna, v prípade ak sú obe premenné parametre funkcie,
     * majú nezhodu v type alebo majú prienik pri množnie aktívnych riadkov daných premenných.
     *
     * @param matrix pôvodná matica susednosti
     *
     * @param i index riadku
     *
     * @param j index stĺpca
     *
     * @param indexes zoznam premenných
     *
     * @param symbolTable symbolická tabuľka
     *
     * @return nová matica susednosti
     */
    private static byte[][] findEdge(byte[][] matrix, int i, int j, ArrayList<Index> indexes, SymbolTable symbolTable) {
        //ak je jedna z premenných global
        if (indexes.get(i).getGlobal() || indexes.get(j).getGlobal()) {
            matrix[i][j] = matrix[j][i] = 1;
            return matrix;
        }
        int length = Math.min(indexes.get(i).getAccess().size(), indexes.get(j).getAccess().size());
        for (int k = 0; k < length; k++) {
            if (!indexes.get(i).getAccess().get(k).equals(indexes.get(j).getAccess().get(k))) {
                //sú "súrodenci"
                matrix[i][j] = matrix[j][i] = 1;
                return matrix;
            }
        }
        Record recordI = iterateTable(symbolTable, indexes.get(i));
        Record recordJ = iterateTable(symbolTable, indexes.get(j));
        if ((recordI.getKind() == Kind.ARRAY_PARAMETER || recordI.getKind() == Kind.PARAMETER) &&
                (recordJ.getKind() == Kind.ARRAY_PARAMETER || recordJ.getKind() == Kind.PARAMETER)) {
            matrix[i][j] = matrix[j][i] = 1;
            return matrix;
        }
        if (!checkTypes(recordI, recordJ)) {
            matrix[i][j] = matrix[j][i] = 1;
            return matrix;
        }
        ArrayList<Integer> activeLines1 = indexes.get(i).getActiveLines();
        ArrayList<Integer> activeLines2 = indexes.get(j).getActiveLines();

        if (isIntersect(activeLines1, activeLines2)) {
            matrix[i][j] = matrix[j][i] = 1;
        } else {
            matrix[i][j] = matrix[j][i] = 0;
        }

        return matrix;
    }

    /**
     * Metóda, ktorá vráti záznam zo symbolickej tabuľky danej premennej na základe informácií o prístupe do správnej
     * symbolickej tabľky.
     *
     * @param symbolTable symbolická tabuľka
     *
     * @param index záznam premennej
     *
     * @return záznam zo symbolickej tabuľky
     */
    private static Record iterateTable(SymbolTable symbolTable, Index index) {
        int i = 0;
        while (i != index.getAccess().size()) {
            symbolTable = symbolTable.getChilds(index.getAccess().get(i));
            i++;
        }
        return symbolTable.getTable().get(index.getKey());
    }

    /**
     * Metóda pre zistenie typovej nezhody medzi dvomi premennými.
     *
     * @param record1 záznam zo symbolickej tabuľky prvej premennej
     *
     * @param record2 záznam zo symbolickej tabuľky druhej premennej
     *
     * @return true, ak nie je typová nezhoda
     *         false, ak je typová nezhoda
     */
    private static boolean checkTypes(Record record1, Record record2) {
        //kontrola, či nejaký z nich je pole
        if ((record1.getKind() == Kind.ARRAY || record1.getKind() == Kind.ARRAY_PARAMETER) &&
                record2.getKind() != Kind.ARRAY && record2.getKind() != Kind.ARRAY_PARAMETER) {
            return false;
        }
        if ((record2.getKind() == Kind.ARRAY || record2.getKind() == Kind.ARRAY_PARAMETER) &&
                record1.getKind() != Kind.ARRAY && record1.getKind() != Kind.ARRAY_PARAMETER) {
            return false;
        }
        // musia mať rovnaký počet *
        if (record1.getType() / 50 != record2.getType() / 50) {
            return false;
        }
        short type1 = record1.getType() >= 50 ? (short) (record1.getType() % 50) : record1.getType();
        short type2 = record2.getType() >= 50 ? (short) (record2.getType() % 50) : record2.getType();

        if (type1 < Type.UNION && type2 < Type.UNION) {
            return true;
        }
        if (type1 == type2) {
            if (type1 == Type.STRUCT || type1 == Type.UNION || type1 == Type.ENUM) {
                return record1.getTypeString().equals(record2.getTypeString());
            }
            return true;
        }
        return false;
    }

    /**
     * Metóda na zistenie, či medzi zoznamami aktívnych riadkov je prienik.
     *
     * @param list1 zoznam aktívnych riadkov prvej premennej
     *
     * @param list2 zoznam aktívnych riadkov druhej premennej
     *
     * @return true, v prípade, ak je prienik medzi množinami riadkov, inak false
     */
    private static boolean isIntersect(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);
        set1.retainAll(set2);

        return !set1.isEmpty();
    }
}
