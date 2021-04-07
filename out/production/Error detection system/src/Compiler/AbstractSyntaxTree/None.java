package Compiler.AbstractSyntaxTree;
//TODO: vymyslieť lepší popis
/**
 * Trieda predstavujúca vrchol, ktorý naznačuje, že nebol nájdený iný vrchol.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class None extends Node {

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
    }

    /**
     * Metóda na zistienie, či daný vrchol je trieda {@code None}.
     *
     * @return true, ak ide o triedu {@code None}
     *         false, ak nejde o triedu {@code None}
     */
    @Override
    public boolean isNone() {
        return true;
    }

}
