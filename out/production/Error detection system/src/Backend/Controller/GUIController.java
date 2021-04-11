package Backend.Controller;

import Backend.Main;
import Backend.ProgramLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
public class GUIController extends Application {

    /** Atribút stage je potrebný pre zobrazovanie okien programu. **/
    static Stage stage;

    /**
     * Privátny konštruktor pre triedu {@code GUIController}.
     */
    public GUIController() {}

    /**
     * Metóda pre spustenie pprogramu vo verzii s grafickým rozhraním.
     *
     * @param args argumenty programu
     */
    public static void runGUI(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.out.println("Pre spustenie grafického rozhranie je potrebné spúšťať program s verziou Javy " +
                    "podporujúcou JavaFX (Java do verzie 10 vrátane)!");
        }
    }

    /**
     * Metóda pre sputenie grafického rozhrania.
     *
     * @param stage kontajner pre okno
     */
    @Override
    public void start(Stage stage) {
        deleteLogFile();
        this.stage = stage;
        try {
            showMainWindow();
        } catch (IOException e) {
            ProgramLogger.createLogger(Main.class.getName()).log(Level.WARNING,
                    "Vyskytla sa chyba pri práci s I/O súbormi!");
            e.printStackTrace();
        }
    }

    /**
     * Metóda pre spustenie obrazovky pre hlavné menu.
     *
     * @throws IOException v prípade ak fxml súbor nebol nájdený
     */
    private void showMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend/MainWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");
        MainController controller = loader.getController();
        controller.setMainStage(stage);

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    /**
     * Metóda pre vymazanie súboru s logmi.
     */
    private void deleteLogFile() {
        File fileError = new File("logs/log-file.log");
        fileError.delete();
    }
}
