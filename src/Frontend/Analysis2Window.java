package Frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Analysis2Window {
    static Stage stage;

    public Analysis2Window(Stage stage) throws IOException {
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Analysis2Window.fxml"));
        stage.setTitle("Systém na detekciu chýb");

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }
    public static void closeStage() {
        stage.close();
    }
}
