package Backend.Controller;

import Backend.Main;
import Backend.ProgramLogger;
import Frontend.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre verziu programu s grafickým rozhraním.
 *
 * <p> V rámci tejto triedy sa spracovávajú jednotlivé možnosti pre analýzu programov.
 *
 * @author Ivan Vykopal
 */
public class GUIController{

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
            SwingUtilities.invokeLater(() -> {
                deleteLogFile();
                MainController.createController(new MainWindow());
            });
        } catch (Exception e) {
            System.out.println("Pre spustenie grafického rozhranie je potrebné spúšťať program s verziou Javy " +
                    "podporujúcou JavaFX (Java do verzie 10 vrátane)!");
        }
    }

    /**
     * Metóda pre vymazanie súboru s logmi.
     */
    private static void deleteLogFile() {
        File fileError = new File("logs/log-file.log");
        fileError.delete();
    }
}
