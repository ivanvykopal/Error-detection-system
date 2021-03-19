package Backend;

import Backend.Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showMainWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Frontend/MainWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Systém na detekciu chýb");
        MainController controller = loader.getController();
        controller.setMainStage(stage);

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }
}