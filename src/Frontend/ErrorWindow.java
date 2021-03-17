package Frontend;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class ErrorWindow {

    public ErrorWindow(Stage stage, ArrayList<String> files) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ErrorWindow.fxml"));
        stage.setTitle("Systém na detekciu chýb");
        files.add(0, "Vyberte súbor");

        stage.setScene(new Scene(root, 1000, 1000));
        ComboBox<String> comboBox = (ComboBox<String>) stage.getScene().lookup("#comboBox");
        comboBox.setItems(FXCollections.observableArrayList(files));
        comboBox.getSelectionModel().selectFirst();
        stage.show();
    }
}
