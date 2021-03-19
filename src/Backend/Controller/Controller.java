package Backend.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Controller {
    protected Stage mainStage;

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

    protected void showErrorWindow(ArrayList<String> files) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Frontend/ErrorWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");
        files.add(0, "Vyberte súbor");

        ErrorController controller = loader.getController();
        controller.setMainStage(stage);
        controller.fillComboBox(files);

        stage.setScene(new Scene(root, 1000, 1000));
        stage.show();
        mainStage.close();
    }

    protected void showErrorWindow(ArrayList<String> files, int fileCount) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Frontend/ErrorWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");
        files.add(0, "Vyberte súbor");

        ErrorController controller = loader.getController();
        controller.setMainStage(stage);
        controller.fillComboBox(files);
        controller.setFileCount(fileCount);

        stage.setScene(new Scene(root, 1000, 1000));
        stage.show();
        mainStage.close();
    }

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
        files.add(0, "Vyberte súbor");

        stage.setScene(new Scene(root, 1000, 1000));
        stage.show();
        mainStage.close();
    }

    public void setMainStage(Stage stage) {
        mainStage = stage;
    }
}
