package Frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainWindow {

    public MainWindow(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        stage.setTitle("Systém na detekciu chýb");

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

}
