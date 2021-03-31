package Backend.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Trieda pre kontrolere pre jednotlivé okná.
 *
 * <p> Obsahuje zároveň aj preddefinované metódy pre spúšťanie jednotlivých okien programu.
 *
 * @author Ivan Vykopal
 */
public class Controller {
    /** Atribút mainStage potrebný pre zobrazovanie okien programu. **/
    protected Stage mainStage;

    /**
     *  Metóda pre spustenie obrazovky pre analyzovanie jedného súboru.
     *
     * @throws IOException v prípade ak fxml súbor nebol nájdený
     */
    protected void showAnalysis1Window() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Frontend/Analysis1Window.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");

        Analysis1Controller controller = loader.getController();
        controller.setMainStage(stage);

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
        mainStage.close();
    }

    /**
     * Metóda pre spustenie obrazovky pre analyzovanie adresáru so zdrojovými kódmi.
     *
     * @throws IOException v prípade ak fxml súbor nebol nájdený
     */
    protected void showAnalysis2Window() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Frontend/Analysis2Window.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");

        Analysis2Controller controller = loader.getController();
        controller.setMainStage(stage);

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
        mainStage.close();
    }

    /**
     * Metóda pre spustenie obrazovky pre vypísanie chýb.
     *
     * @param files zoznam súborov, v ktorých sa nachádza aspoň jedna chyba
     *
     * @param fileCount počet analyzovaných zdrojových kódov
     *
     * @throws IOException v prípade ak fxml súbor nebol nájdený
     */
    protected void showErrorWindow(ArrayList<String> files, int fileCount) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Frontend/ErrorWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");

        ErrorController controller = loader.getController();
        controller.setMainStage(stage);
        controller.fillComboBox(files);
        controller.setFileCount(fileCount);

        stage.setScene(new Scene(root, 1000, 1000));
        stage.show();
        mainStage.close();
    }

    /**
     * Metóda pre spustenie obrazovky pre hlavné menu.
     *
     * @throws IOException v prípade ak fxml súbor nebol nájdený
     */
    protected void showMainWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Frontend/MainWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");
        MainController controller = loader.getController();
        controller.setMainStage(stage);

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
        mainStage.close();
    }

    /**
     * Metóda pre spustenie obrazovky pre zobrazenie štatistík.
     *
     * @param table tabuľka s chybami pre jednotlivé zdrojové kódy
     *
     * @param count počet analyzovaných zdrojových kódov
     *
     * @param files zoznam súborov, v ktorých sa nachádza aspoň jedna chyba
     *
     * @throws IOException v prípade ak fxml súbor nebol nájdený
     */
    protected void showStatisticsWindow(HashMap<String, ArrayList<TableRecord>> table, int count, ArrayList<String> files) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Frontend/StatisticsWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");
        StatisticsController controller = loader.getController();
        controller.setMainStage(stage);
        controller.setFileCount(count);
        controller.fillComboBox(files);
        controller.setTable(table);
        controller.fillTables();

        stage.setScene(new Scene(root, 1000, 1000));
        stage.show();
        mainStage.close();
    }

    /**
     * Metóda pre nastavenie kontajnera pre okno.
     *
     * @param stage kontajner pre okno
     */
    public void setMainStage(Stage stage) {
        mainStage = stage;
    }
}
