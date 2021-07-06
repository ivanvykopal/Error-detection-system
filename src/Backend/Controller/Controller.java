package Backend.Controller;

import java.io.File;

/**
 * Trieda pre kontrolere pre jednotlivé okná.
 *
 * <p> Obsahuje zároveň aj preddefinované metódy pre spúšťanie jednotlivých okien programu.
 *
 * @author Ivan Vykopal
 */
public class Controller {

    /**
     * Metóda pre vymazanie vybraných súborov.
     */
    protected static void deleteFiles() {
        File fileError = new File("errors.csv");
        fileError.delete();
        File fileVariables = new File("variables.csv");
        fileVariables.delete();
        File fileErrorTotal = new File("total_statistics.csv");
        fileErrorTotal.delete();
        File fileAnalyzing = new File("unanalyzed_files.txt");
        fileAnalyzing.delete();
        File fileStatistics = new File("program_statistics.csv");
        fileStatistics.delete();
    }
}
