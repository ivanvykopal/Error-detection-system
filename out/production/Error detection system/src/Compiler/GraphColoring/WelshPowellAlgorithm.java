package Compiler.GraphColoring;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Trieda pre farbenie grafov pomocou Welsh-Powell algoritmu.
 *
 * @author Ivan Vykopal
 */
public final class WelshPowellAlgorithm {
    /** Atribút ratings obsahuje informácie o jednotlivých vrcholoch grafu. **/
    static ArrayList<Rating> ratings = new ArrayList<>();

    /** Atribút colors obsahuje informácie o farbách jednotlivých vrcholoch. **/
    static int[] colors;

    /**
     * Privátny konštruktor.
     */
    private WelshPowellAlgorithm() {
    }

    /**
     * Metóda pre vyprázdnenie informácií o jednotlivých vrcholoch grafu.
     */
    private static void clear() {
        ratings = new ArrayList<>();
    }

    /**
     * Metóda pre vrcholové farbenie grafu.
     *
     * @param size počet premenných
     *
     * @param matrix matica susednosti
     *
     * @return zoznam farieb jednotlivých vrcholov
     */
    public static int[] graphColoring(int size, byte[][] matrix) {
        clear();
        colors = new int[size];
        int color = 1;

        //Inicializácia
        for (int i = 0; i < size; i++) {
            ratings.add(new Rating(0, i));
        }

        //Najdenie stupnov vrcholov
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] == 1) {
                    ratings.get(i).setRate(ratings.get(i).getRate() + 1);
                }
            }
        }

        //usporiadanie vrcholov podľa ich stupňa
        Collections.sort(ratings);

        int pocet = 0;
        boolean problem = false;
        while (true) {
            for (int i = 0; i < size; i++) {
                if (colors[ratings.get(i).getPosition()] == 0) {
                    for (int j = 0; j < size; j++) {
                        if (matrix[ratings.get(i).getPosition()][j] == 1 && colors[j] == color) {
                            problem = true;
                            break;
                        }
                    }
                    if (!problem) {
                        colors[ratings.get(i).getPosition()] = color;
                        pocet++;
                        if (pocet == size) {
                            break;
                        }
                    }
                    problem = false;
                }
            }
            if (pocet == size) {
                break;
            }
            color++;
        }

        return colors;
    }

}