package Backend.Controller;

import javafx.fxml.FXML;
import java.io.IOException;

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
     *
     * @throws IOException
     */
    @FXML
    public void analysis1() throws IOException {
        showAnalysis1Window();
    }

    /**
     * Metóda pre spracovanie stlačenia tlačidla pre analyzovania adresára so zdrojovými kódmi.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí obrazovka pre analýzu viacerých zdrojových kódov.
     *
     * @throws IOException
     */
    @FXML
    public void analysis2() throws IOException {
        showAnalysis2Window();
    }

}
