package Backend;

import Backend.Controller.ConsoleController;
import Backend.Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Hlavná trieda pre spustenie programu.
 *
 * @author Ivan Vykopal
 */
public class Main extends Application {

    /** Atribút stage je potrebný pre zobrazovanie okien programu. **/
    static Stage stage;

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
     * Hlavná metóda pre spustenie programu.
     *
     * @param args argumenty programu
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            launch(args);
        } else {
            if (args.length == 1) {
                switch (args[0]) {
                    case "console" :
                        ConsoleController.runConsole();
                        break;
                    case "gui" :
                        launch(args);
                        break;
                    default:
                        System.out.println("Nesprávny argument programu!");
                        break;
                }
            } else {
                System.out.println("Program požaduje len jeden argument!");
            }
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