package Frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Analysis1Window {
    static Stage stage;

    public Analysis1Window(Stage stage) throws IOException {
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Analysis1Window.fxml"));
        stage.setTitle("Systém na detekciu chýb");

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    public static void closeStage() {
        stage.close();
    }
}
