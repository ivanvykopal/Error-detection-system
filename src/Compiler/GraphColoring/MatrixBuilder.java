package Compiler.GraphColoring;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Errors.ErrorRecord;
import Compiler.SymbolTable.Kind;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MatrixBuilder {
    private String file;

    public MatrixBuilder(SymbolTable symbolTable, ErrorDatabase errorDatabase, String file) {
        this.file = file;

        for (SymbolTable tab : symbolTable.getChilds()) {
            ArrayList<Index> indexes = findGlobal(symbolTable);
            ArrayList<Integer> list = new ArrayList<>();
            createIndexes(tab, list, indexes);
            createMatrix(tab, indexes, errorDatabase);
        }
    }

    private void createMatrix(SymbolTable symbolTable, ArrayList<Index> indexes, ErrorDatabase errorDatabase) {
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

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.print("\n");
        }

        int[] colors = WelshPowellAlgorithm.graphColoring(rows, matrix);

        for (int i = 0; i < rows; i++) {
            System.out.println("Vrchol " + i + " má farbu " + colors[i] + "!");
        }

        ArrayList<String>[] variables = new ArrayList[rows];
        for (int i = 0; i < rows; i++) {
            variables[i] = new ArrayList<>();
        }

        int maxColor = 0;
        for (int i = 0; i < rows; i++) {
            if ((colors[i] - 1) > maxColor) {
                maxColor = colors[i] - 1;
            }
            variables[colors[i] - 1].add(indexes.get(i).getKey());
        }

        if (maxColor != rows - 1) {
            errorDatabase.addErrorMessage(0, Error.getError("E-RP-07"), "E-RP-07");
        }

        for (int i = 0; i <= maxColor; i++) {
            System.out.print(i + "\t");
            for (String str : variables[i]) {
                System.out.print(str + ", ");
            }
            System.out.print("\n");
        }

        createVariables(maxColor, variables);

    }

    private void createIndexes(SymbolTable symbolTable, ArrayList<Integer> list, ArrayList<Index> indexes) {
        for (String key : symbolTable.getTable().keySet()) {
            addItem(key, new ArrayList<>(list), indexes, symbolTable);
        }
        int i = 0;
        for (SymbolTable tab : symbolTable.getChilds()) {
            list.add(i);
            createIndexes(tab, list, indexes);
            list.remove(list.size() - 1);
            i++;
        }

    }

    private void addItem(String key, ArrayList<Integer> list, ArrayList<Index> indexes, SymbolTable symbolTable) {
        Record record = symbolTable.getTable().get(key);
        Index index = new Index(key);
        index.setAccess(list);
        index.setActiveLines(findActiveLines(record));
        indexes.add(index);
    }

    private byte[][] findEdge(byte[][] matrix, int i, int j, ArrayList<Index> indexes, SymbolTable symbolTable) {
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

    private Record iterateTable(SymbolTable symbolTable, Index index) {
        int i = 0;
        while (i != index.getAccess().size()) {
            symbolTable = symbolTable.getChilds(index.getAccess().get(i));
            i++;
        }
        return symbolTable.getTable().get(index.getKey());
    }

    private ArrayList<Index> findGlobal(SymbolTable symbolTable) {
        ArrayList<Index> indexes = new ArrayList<>();
        HashMap<String, Record> table = symbolTable.getTable();
        for (String key: table.keySet()) {
            Record record = table.get(key);
            if (record.getKind() == Kind.VARIABLE || record.getKind() == Kind.ARRAY) {
                Index index = new Index(key);
                index.setAccess(new ArrayList<>());
                index.setGlobal(true);
                indexes.add(index);
            }
        }
        return indexes;
    }

    private boolean checkTypes(Record record1, Record record2) {
        //kontrola, či nejaký z nich je pole
        if ((record1.getKind() == Kind.ARRAY || record1.getKind() == Kind.ARRAY_PARAMETER) &&
                record2.getKind() != Kind.ARRAY && record2.getKind() != Kind.ARRAY_PARAMETER) {
            return false;
        }
        if ((record2.getKind() == Kind.ARRAY || record2.getKind() == Kind.ARRAY_PARAMETER) &&
                record1.getKind() != Kind.ARRAY && record1.getKind() == Kind.ARRAY_PARAMETER) {
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

    private ArrayList<Integer> findActiveLines(Record record) {
        ArrayList<Integer> activeLines = new ArrayList<>();
        int initializationLength = record.getInitializationLines().size();
        int usageLength = record.getUsageLines().size();
        int i = 0, j = 0;
        int init, usage;
        if (record.getUsageLines().size() == 0) {
            return activeLines;
        }
        while (true) {
            if (i == initializationLength && j == usageLength) {
                break;
            }
            if (i == initializationLength) {
                i--;
            }
            if (j == usageLength) {
                j--;
            }
            init = record.getInitializationLines().get(i);
            usage = record.getUsageLines().get(j);
            if ((i + 1) < initializationLength && usage > record.getInitializationLines().get(i + 1)) {
                i++;
                continue;
            }
            if ((i + 1) >= initializationLength && (j + 1) < usageLength) {
                addActiveLines(init, record.getUsageLines().get(usageLength - 1), activeLines);
                break;
            } else if ((i + 1) < initializationLength && (j + 1) < usageLength &&
                    record.getInitializationLines().get(i + 1) >= record.getUsageLines().get(j + 1)) {
                j++;
            } else {
                addActiveLines(init, usage, activeLines);
                i++;
                j++;
            }
        }

        return activeLines;
    }

    private void addActiveLines(int start, int end, ArrayList<Integer> activeLines) {
        int length = activeLines.size();
        if (length != 0 && activeLines.get(length - 1) == start) {
            start++;
        }
        for (int i = start; i <= end; i++) {
            activeLines.add(i);
        }
    }

    private boolean isIntersect(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);
        set1.retainAll(set2);

        return !set1.isEmpty();
    }

    private void createVariables(int size, ArrayList<String>[] variables) {
        try {
            File fileVariables = new File("variables.csv");
            fileVariables.createNewFile();

            FileWriter fileWriter = new FileWriter(fileVariables, true);
            for (int i = 0; i <= size; i++) {
                fileWriter.write(file + ", " + String.join("; ", variables[i]) + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Chyba");
        }
    }
}
