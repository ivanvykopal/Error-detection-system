package Backend.Controller;

import Backend.ProgramLogger;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre MainWindow.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class MainController extends Controller {

    /**
     * Metóda pre spracovanie stlačenia tlačidla pre analyzovania jedného súboru.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí obrazovka pre analýzu jedného súboru.
     */
    @FXML
    public void analysis1() {
        try {
            showAnalysis1Window();
        } catch (IOException e) {
            ProgramLogger.createLogger(MainController.class.getName()).log(Level.WARNING,
                    "Problém pri načítaní showAnalysis1Window()!");
        }
    }

    /**
     * Metóda pre spracovanie stlačenia tlačidla pre analyzovania adresára so zdrojovými kódmi.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí obrazovka pre analýzu viacerých zdrojových kódov.
     */
    @FXML
    public void analysis2() {
        try {
            showAnalysis2Window();
        } catch (IOException e) {
            ProgramLogger.createLogger(MainController.class.getName()).log(Level.WARNING,
                    "Problém pri načítaní showAnalysis2Window()!");
        }
    }

}
