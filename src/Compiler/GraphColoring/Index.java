package Compiler.GraphColoring;

import java.util.ArrayList;

public class Index {
    private String key;
    private boolean global = false;
    private ArrayList<Integer> access;
    private ArrayList<Integer> activeLines;

    public Index(String key) {
        this.key = key;
        access = new ArrayList<>();
        activeLines = new ArrayList<>();
    }

    public void addAccess(int index) {
        access.add(index);
    }

    public void setAccess(ArrayList<Integer> access) {
        this.access = access;
    }

    public ArrayList<Integer> getAccess() {
        return access;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean getGlobal() {
        return global;
    }

    public void setActiveLines(ArrayList<Integer> activeLines) {
        this.activeLines = activeLines;
    }

    public ArrayList<Integer> getActiveLines() {
        return activeLines;
    }
}
