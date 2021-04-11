package Compiler.GraphColoring;

/**
 * Trieda predstavujúca prvok obsahujúci pozíciu vrcholu v matici susednosti a zároveň predstavujúci stupeň
 * daného vrcholu.
 *
 * @author Ivan Vykopal
 */
public class Rating implements Comparable {
    /** Atribút rate predstavuje stupeň vrcholu (počet hrán, ktoré doneho vchádzajú a vychádzajú).**/
    private int rate;

    /** Atribút position predtsavuje pozíciu vrcholu v matici susednosti. **/
    private int position;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Rating} a inicilizuje jej atribúty.
     *
     * @param rate stupeň vrcholu
     *
     * @param position pozícia vrcholu v matici susednosti
     */
    public Rating(int rate, int position) {
        this.rate = rate;
        this.position = position;
    }

    /**
     * Metóda pre nastavenie atribútu predstavujúceho stupeň vrcholu.
     *
     * @param rate stupeň vrcholu
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Metóda pre zistenie stupňa vrcholu.
     *
     * @return stupeň vrcholu
     */
    public int getRate() {
        return rate;
    }

    /**
     * Metóda pre nastavenie pozície vrcholu v rámci matici susednosti.
     *
     * @param pos pozícia vrcholu v matici susednosti
     */
    public void setPosition(int pos) {
        position = pos;
    }

    /**
     * Metóda pre zistenie pozície vrcholu v rámci matici susednosti.
     *
     * @return pozícia vrcholu v matici susednosti
     */
    public int getPosition() {
        return position;
    }

    /**
     * Metóda pre porovnávanie dvoch tried typu {@code Rating}.
     *
     * @param o objekt, určený pre porovnávanie
     *
     * @return
     * {@code
     *          0 v prípade, ak sú stupne vrcholov rovnaké,
     *         < 0 v prípade, ak atribút rate je menší ako stupeň vrcholu, s ktorým porovnávame,
     *         > 0 v prípade, ak atribút rate je väčší ako stupeň vrcholu, s ktorým porovnávame
     * }
     */
    @Override
    public int compareTo(Object o) {
        int oRate = ((Rating) o).getRate();
        return oRate - this.rate;
    }
}
