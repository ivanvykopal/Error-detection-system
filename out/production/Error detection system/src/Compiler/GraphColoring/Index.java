package Compiler.GraphColoring;

import java.util.ArrayList;

/**
 * Trieda obsahujúca informácie o premennej zo symbolickej tabuľky.
 *
 * @author Ivan Vykopal
 */
public class Index {
    /** Atribút key prdstavuje názov premennej. **/
    private String key;

    /** Atribút declarationLine predstavuje riadok deklarácie premennej. **/
    private int declarationLine;

    /** Atribút type predstavuje typ premennej. **/
    private String type;

    /** Atribút global obsahuje informáciu o tom, či premenná je alebo nie je globálna.**/
    private boolean global = false;

    /** Atribút access predstavuje informácie o uložení premennej v zreťazenom zozname tabuliek. **/
    private ArrayList<Integer> access;

    /** Atribút activeLines predstavuje zoznam riadkov, na ktorých je daná premenná aktívna. **/
    private ArrayList<Integer> activeLines;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Index} a inicilizuje jej atribúty.
     *
     * @param key názov premennej
     *
     * @param line riadok deklarácie premennej
     *
     * @param type typ premennej
     */
    public Index(String key, int line, String type) {
        this.key = key;
        this.declarationLine = line;
        this.type = type;
        access = new ArrayList<>();
        activeLines = new ArrayList<>();
    }

    /**
     * Metóda na pridanie indexu do premennej access ( pridanie informácie, v ktorej časti symbolickej
     * tabuľky sa nchádza.
     *
     * @param index index nasledujúcej symbolickej tabuľky v zreťazenom zozname symbolických tabuliek
     */
    public void addAccess(int index) {
        access.add(index);
    }

    //TODO: premyslieť názov
    /**
     * Metóda na nastavenie informácií o uložení premennej v zreťazenom zozname symbolických tabuliek.
     *
     * @param access zoznam indexov prístupov
     */
    public void setAccess(ArrayList<Integer> access) {
        this.access = access;
    }

    /**
     * Metóda na zistenie informácií o uložení premennej.
     *
     * @return zoznam indexov prístupov
     */
    public ArrayList<Integer> getAccess() {
        return access;
    }

    /**
     * Metóda na nastavenie názvu premennej.
     *
     * @param key názov premennej
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Metóda na zistenie názvu premennej.
     *
     * @return názov premennej
     */
    public String getKey() {
        return key;
    }

    /**
     * Metóda na nastavenie informácie, či premenná je globálna.
     *
     * @param global true, ak premenná je globálne, inak false
     */
    public void setGlobal(boolean global) {
        this.global = global;
    }

    /**
     * Metóda na zistenie informácie, či premenná je globálna.
     *
     * @return true, ak premenná je globálne, inak false
     */
    public boolean getGlobal() {
        return global;
    }

    /**
     * Metóda na nastavenie aktívnych riadkov premennej.
     *
     * @param activeLines zoznam aktívnych riadokov premennej
     */
    public void setActiveLines(ArrayList<Integer> activeLines) {
        this.activeLines = activeLines;
    }

    /**
     * Metóda na zistenie aktívnych riadkov premennej.
     *
     * @return zoznam aktívnych riadokov premennej
     */
    public ArrayList<Integer> getActiveLines() {
        return activeLines;
    }

    /**
     * Metóda na zistenie riadku deklarácie premennej.
     *
     * @return riadok deklarácie premennej
     */
    public int getDeclarationLine() {
        return declarationLine;
    }

    /**
     * Metóda na nastavenie riadku deklarácie premennej.
     *
     * @param declarationLine riadok deklarácie premennej
     */
    public void setDeclarationLine(int declarationLine) {
        this.declarationLine = declarationLine;
    }

    /**
     * Metóda na zistenie typu premennej.
     *
     * @return typ premennej
     */
    public String getType() {
        return type;
    }

    /**
     * Metóda na nastavenie typu premennej.
     *
     * @param type typ premennej
     */
    public void setType(String type) {
        this.type = type;
    }
}
