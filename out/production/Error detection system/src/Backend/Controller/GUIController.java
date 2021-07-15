package Backend.Controller;

import Backend.InternationalizationClass;
import Frontend.LanguageWindow;
import Frontend.MainWindow;
import javax.swing.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * Trieda predstavujúca controller pre verziu programu s grafickým rozhraním.
 *
 * <p> V rámci tejto triedy sa spracovávajú jednotlivé možnosti pre analýzu programov.
 *
 * @author Ivan Vykopal
 */
public class GUIController {

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private static final ResourceBundle bundle = InternationalizationClass.getBundle();

    /**
     * Privátny konštruktor pre triedu {@code GUIController}.
     */
    public GUIController() {}

    /**
     * Metóda pre spustenie pprogramu vo verzii s grafickým rozhraním.
     */
    public static void runGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }
        SwingUtilities.invokeLater(() -> {
                deleteLogFile();
                LanguageController.createController(new LanguageWindow());
            });

    }

    /**
     * Metóda pre vymazanie súboru s logmi.
     */
    private static void deleteLogFile() {
        File fileError = new File("logs/log-file.log");
        fileError.delete();
    }
}
